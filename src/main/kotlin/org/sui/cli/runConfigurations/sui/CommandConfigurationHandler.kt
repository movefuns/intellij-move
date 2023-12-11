package org.sui.cli.runConfigurations.sui

import com.intellij.psi.PsiElement
import org.sui.cli.MoveProject
import org.sui.cli.runConfigurations.producers.CommandConfigurationProducerBase
import org.sui.cli.runConfigurations.producers.CommandLineFromContext
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

    fun configurationFromLocation(location: PsiElement): CommandLineFromContext? {
        val function =
            CommandConfigurationProducerBase.findElement<MvFunction>(location, true)
                ?.takeIf(this::functionPredicate)
                ?: return null
        val moveProject = function.moveProject ?: return null

        val functionId = function.functionId(moveProject) ?: return null
        val workingDirectory = moveProject.contentRootPath

        val arguments = mutableListOf<String>()

        arguments.addAll(listOf("--function", functionId))

        val commandLine = SuiCommandLine(subCommand, arguments, workingDirectory)
        return CommandLineFromContext(
            function,
            configurationName(functionId),
            commandLine
        )
    }

    abstract fun getFunctionCompletionVariants(moveProject: MoveProject): Collection<String>

    abstract fun getFunction(moveProject: MoveProject, functionQualName: String): MvFunction?

    abstract fun getFunctionByCmdName(moveProject: MoveProject, functionCmdName: String): MvFunction?

    abstract fun getFunctionParameters(function: MvFunction): List<MvFunctionParameter>

    fun generateCommand(
        moveProject: MoveProject,
        profileName: String,
        functionCall: FunctionCall
    ): RsResult<String, String> {
        val functionId = functionCall.functionId(moveProject) ?: return RsResult.Err("FunctionId is null")

        val typeParams = functionCall.typeParams
            .mapNotNull { it.value }.flatMap { listOf("--type-args", it) }
        val params = functionCall.valueParams
            .mapNotNull { it.value?.cmdText() }.flatMap { listOf("--args", it) }

        val commandArguments = listOf(
            subCommand.split(' '),
            listOf("--function", functionId),
            typeParams,
            params
        ).flatten()
        return RsResult.Ok(commandArguments.joinToString(" "))
    }

    fun parseCommand(
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

        val transaction = FunctionCall.template(function)

        val typeParameterNames = function.typeParameters.mapNotNull { it.name }
        for ((name, value) in typeParameterNames.zip(callArgs.typeArgs)) {
            transaction.typeParams[name] = value
        }

        val parameterBindings = getFunctionParameters(function).map { it.bindingPat }
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
