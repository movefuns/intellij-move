
## sui client 

```shell

Usage: sui client [OPTIONS] [COMMAND]

Commands:
    active-address        Default address used for commands when none specified
    active-env            Default environment used for commands when none specified
    addresses             Obtain the Addresses managed by the client
    call                      Call Move function
    chain-identifier      Query the chain identifier from the rpc endpoint
    dynamic-field         Query a dynamic field by its address
    envs                  List all Sui environments
    execute-signed-tx     Execute a Signed Transaction. This is useful when the user prefers to sign elsewhere and use this command to execute
    gas                   Obtain all gas objects owned by the address
    merge-coin            Merge two coin objects into one coin
    new-address           "Generate new address and keypair with keypair scheme flag {ed25519 | secp256k1 | secp256r1} with optional derivation path, default to m/44'/784'/0'/0'/0' for
                          ed25519 or m/54'/784'/0'/0/0 for secp256k1 or m/74'/784'/0'/0/0 for secp256r1. Word length can be { word12 | word15 | word18 | word21 | word24} default to word12 if not specified"
    new-env               Add new Sui environment
    object                Get object info
    objects               Obtain all objects owned by the address
    pay                   Pay coins to recipients following specified amounts, with input coins. Length of recipients must be the same as that of amounts
    pay-all-sui           Pay all residual SUI coins to the recipient with input coins, after deducting the gas cost. The input coins also include the coin for gas payment, so no extra gas
    coin is required
    pay-sui               Pay SUI coins to recipients following following specified amounts, with input coins. Length of recipients must be the same as that of amounts. The input coins also
    include the coin for gas payment, so no extra gas coin is required
    publish               Publish Move modules
    replay-transaction    Replay a given transaction to view transaction effects. Set environment variable MOVE_VM_STEP=1 to debug.
        replay-batch          Replay transactions listed in a file.
        replay-checkpoint     Replay all transactions in a range of checkpoints.
        split-coin            Split a coin object into multiple coins
    switch                Switch active address and network(e.g., devnet, local rpc server)
        tx-block              Get the effects of executing the given transaction block
        transfer              Transfer object
        transfer-sui          Transfer SUI, and pay gas with the same SUI coin object. If amount is specified, only the amount is transferred; otherwise the entire object is transferred
        upgrade               Upgrade Move modules
        verify-bytecode-meter  Run the bytecode verifier on the package
        verify-source         Verify local Move packages against on-chain packages, and optionally their dependencies
        help                  Print this message or the help of the given subcommand(s)
    
        Options:
            --client.config <CONFIG>  Sets the file storing the state of our user accounts (an empty one will be created if missing)
            --json                  Return command outputs in json format
        -y, --yes
        -h, --help                    Print help

```


## sui move

```shell

Usage: sui move [OPTIONS] <COMMAND>

Commands:
  build
  coverage  Inspect test coverage for this package. A previous test run with the `--coverage` flag must have previously been run
  disassemble
  new       Create a new Move package with name `name` at `path`. If `path` is not provided the package will be created in the directory `name`
  prove     "Run the Move Prover on the package at `path`. If no path is provided defaults to current directory. Use `.. prove .. -- <options>` to pass on options to the
                prover"
  test      Run Move unit tests in this package
  help      Print this message or the help of the given subcommand(s)

Options:
  -p, --path <PACKAGE_PATH>                     Path to a package which the command should be run with respect to
  -d, --dev                                     Compile in 'dev' mode. The 'dev-addresses' and 'dev-dependencies' fields will be used if this flag is set. This flag is useful for
                                                development of packages that expose named addresses that are not set to a specific value
    --test                                  Compile in 'test' mode. The 'dev-addresses' and 'dev-dependencies' fields will be used along with any code in the 'tests' directory
    --doc                                   Generate documentation for packages
    --abi                                   Generate ABIs for packages
    --install-dir <INSTALL_DIR>             Installation directory for compiled artifacts. Defaults to current directory
    --force                                 Force recompilation of all packages
    --fetch-deps-only                       Only fetch dependency repos to MOVE_HOME
    --skip-fetch-latest-git-deps            Skip fetching latest git dependencies
    --default-move-flavor <DEFAULT_FLAVOR>  Default flavor for move compilation, if not specified in the package's config
    --default-move-edition <DEFAULT_EDITION>  Default edition for move compilation, if not specified in the package's config
    --dependencies-are-root                 If set, dependency packages are treated as root packages. Notably, this will remove warning suppression in dependency packages
  -h, --help                                    Print help
  -V, --version                                 Print version

```