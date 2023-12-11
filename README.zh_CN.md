# Sui Move 语言 IntelliJ 平台插件

## 安装

在你的 IDE 中打开 `Settings > Plugins > Marketplace`，搜索 Sui Move Language 并安装该插件。

## 特性

- 语法高亮
- 代码格式化
- 跳转到定义
- 类型推断
- 执行自定义命令
- 一些快捷按钮
- `sui` 二进制集成

## 依赖

对于 Move.toml 文件中指定的 git 依赖，你需要手动运行 move package build 来填充 build/ 目录。

## 兼容的 IDE

从 2022.3 版本开始的所有基于 IntelliJ 的 IDE。

## 使用方法

### 设置 Sui Cli 路径

打开 `Settings > Languages&Frameworks > Sui Move Language`，点击文件选择并选择有效的 sui cli 路径。
然后版本标签将显示 sui cli 的版本。

![img.png](docs/static/select-sui-path.png)

### 使用 ToolWindow

该插件提供了一个专用的 ToolWindow，具有以下功能：
![img.png](docs/static/init.png)

- 刷新项目:

  ![img.png](docs/static/img.png) ： 同步项目的最新状态。

- 获取当前活跃地址:

  ![img_1.png](docs/static/img_1.png)： 显示当前活跃地址信息。

- 切换账户:

  ![img_2.png](docs/static/img_2.png)： 在不同账户间切换。

- 切换网络:

  ![img_3.png](docs/static/img_3.png)： 允许在不同网络环境之间切换，例如从开发网络切换到测试网络。

### 执行自定义命令:

点击工具栏中的`Edit Configurations` 来配置自定义命令,点击左上角的`+`，找到`Sui Command`并点击添加一个自定义命令
![img.png](docs/static/add-configure.png)

在配置面板中，你可以设置名称和命令。保存后，你可以点击工具栏中的![img_2.png](docs/static/run-button.png)按钮来执行命令。

> 注意: 请确保`Working directory`设置为项目根目录。

![img_1.png](docs/static/configure-panel.png)

## 注意事项

在使用插件功能前，请确保项目正确设置。
插件的功能取决于项目的当前状态，请保持项目的更新。

## 获取帮助

在使用插件过程中遇到任何问题，请在 [GitHub](http://github.com/moveFuns/intellij-move) 上提出问题。