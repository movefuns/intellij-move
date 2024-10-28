package org.sui.cli.runConfigurations.sui

import com.intellij.psi.PsiElement
import org.sui.cli.MoveProject
import org.sui.cli.runConfigurations.SuiCommandLine
import org.sui.cli.runConfigurations.producers.CommandConfigurationProducerBase
import org.sui.cli.runConfigurations.producers.SuiCommandLineFromContext
import org.sui.lang.core.psi.MvFunction
import org.sui.lang.core.psi.MvFunctionParameter
import org.sui.lang.core.psi.ext.functionId
import org.sui.lang.core.psi.typeParameters
import org.sui.lang.core.types.infer.inference
import org.sui.lang.moveProject
import org.sui.stdext.RsResult

abstract class CommandConfigurationHandler {

    abstract val subCommand: String

    abstract fun functionPredicate(function: MvFunction): Boolean

    abstract fun configurationName(functionId: String): String

    fun configurationFromLocation(location: PsiElement): SuiCommandLineFromContext? {
        val function =
            CommandConfigurationProducerBase.findElement<MvFunction>(location, true)
                ?.takeIf(this::functionPredicate)
                ?: return null
        val moveProject = function.moveProject ?: return null

        val functionId = function.functionId(moveProject) ?: return null
        val profileName = moveProject.profiles.firstOrNull()
        val workingDirectory = moveProject.contentRootPath

        val arguments = mutableListOf<String>()

        val functionQualList = functionId.split("::")
        val moduleName = functionQualList[1]
        val functionName = functionQualList[2]

        // arguments.addAll(listOf("--package", functionId))
        arguments.addAll(listOf("--module", moduleName))
        arguments.addAll(listOf("--function", functionId))

        val commandLine = SuiCommandLine(subCommand, arguments, workingDirectory)
        return SuiCommandLineFromContext(
            function,
            configurationName(functionId),
            commandLine
        )
    }

    abstract fun getFunctionCompletionVariants(moveProject: MoveProject): Collection<String>

    abstract fun getFunctionItem(moveProject: MoveProject, functionQualName: String): MvFunction?

    abstract fun getFunctionByCmdName(moveProject: MoveProject, functionCmdName: String): MvFunction?

    abstract fun getFunctionParameters(function: MvFunction): List<MvFunctionParameter>

    fun generateCommand(
        moveProject: MoveProject,
        functionCall: FunctionCall,
        signerAccount: String?,
    ): RsResult<String, String> {
        val functionId = functionCall.functionId(moveProject) ?: return RsResult.Err("FunctionId is null")

        val typeParams = functionCall.typeParams
            .mapNotNull { it.value }.flatMap { listOf("--type-args", it) }
        val params = functionCall.valueParams
            .mapNotNull { it.value?.cmdText() }.flatMap { listOf("--args", it) }

        val functionQualList = functionId.split("::")
        val moduleName = functionQualList[1]
        val functionName = functionQualList[2]

        val gasArguments = functionCall.gasId?.let { listOf("", it) }
        val gasBudgetArguments = functionCall.gasBudget?.let { listOf("--gas-budget", it) }

        val commandArguments = listOf(
            subCommand.split(' '),
            listOf("--package", functionCall.packageId),
            listOf("--module", moduleName),
            listOf("--function", functionName),
            gasArguments ?: emptyList(),
            gasBudgetArguments ?: emptyList(),
            typeParams,
            params
        )

        val command = commandArguments.flatten().joinToString(" ")
        return RsResult.Ok(command)
    }

    fun parseTransactionCommand(
        moveProject: MoveProject,
        command: String
    ): RsResult<Pair<String, FunctionCall>, String> {
        val res = FunctionCallParser.parse(command, subCommand)
        val callArgs = when (res) {
            is RsResult.Ok -> res.ok
            is RsResult.Err -> return RsResult.Err("malformed command error '${res.err}'")
        }
        val profileName = callArgs.profile

        val functionId = callArgs.functionId
        val function = getFunctionByCmdName(moveProject, functionId)
            ?: return RsResult.Err("function with this functionId does not exist in the current project")

        val transaction = FunctionCall.template(function, callArgs.packageId, callArgs.gasId, callArgs.gasBudget)

        val typeParameterNames = function.typeParameters.mapNotNull { it.name }
        for ((name, value) in typeParameterNames.zip(callArgs.typeArgs)) {
            transaction.typeParams[name] = value
        }

        val parameterBindings = getFunctionParameters(function).map { it.patBinding }
        val inference = function.inference(false)
        for ((binding, valueWithType) in parameterBindings.zip(callArgs.args)) {
            val name = binding.name
            val value = valueWithType.split(':')[1]
            val ty = inference.getPatType(binding)
            transaction.valueParams[name] = FunctionCallParam(value, FunctionCallParam.tyTypeName(ty))
        }

        return RsResult.Ok(Pair(profileName, transaction))
    }
}
