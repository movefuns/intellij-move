package org.sui.cli.runConfigurations.sui;


public enum SuiCommandFunction {

    ACTIVE_ADDRESS("active-address"),
    ACTIVE_ENV("active-env"),
    ADDRESSES("addresses"),
    ENVS("envs"),
    OBJECTS("objects"),
    ;
    // 添加一个参数
    private final String function;
    SuiCommandFunction(String function) {
        this.function = function;
    }

    // 通过枚举获取参数
    public String getFunction() {
        return function;
    }

        //通过function获取枚举
    public static SuiCommandFunction getFunction(String function) {
        for (SuiCommandFunction suiCommandFunction : SuiCommandFunction.values()) {
            if (suiCommandFunction.getFunction().equals(function)) {
                return suiCommandFunction;
            }
        }
        return null;
    }
}
