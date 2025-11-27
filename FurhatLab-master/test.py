import webbrowser
ROBOT_IP = "192.168.1.175"

print("请在机器人上完成以下步骤：")
print("1. 打开机器人管理界面")
webbrowser.open(f"http://{ROBOT_IP}")

print("""
操作步骤：
1. 登录机器人管理界面
2. 进入 'Skills' 或 '应用' 页面
3. 执行以下操作之一：

选项A - 上传自定义技能：
   - 点击 'Upload Skill' 
   - 选择您的技能文件 (.jar 或 .zip)
   - 等待上传完成

选项B - 启动内置技能：
   - 在技能列表中找到可用技能
   - 点击 'Start' 或 '运行'
   - 确保技能状态显示为 'Running'

选项C - 安装示例技能：
   - 查找 'Demo Skill' 或 'Example Skill'
   - 安装并启动它

4. 确认技能完全启动并显示 'Ready' 状态
""")