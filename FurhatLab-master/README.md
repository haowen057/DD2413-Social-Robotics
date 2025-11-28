详细对话服务流程
1. 初始问候阶段
text
机器人: "Hello, welcome to KTH Hotel. Do you want to book a room?"

用户选择:
├── 是 (Yes) → 进入入住日期询问
└── 否 (No) → 返回空闲状态
2. 入住日期收集
text
机器人: "When would you like to check in? You can say today, tomorrow, or a specific day like Monday."

用户回答:
├── today/tomorrow → 直接设置日期
├── 具体星期几 → 设置对应日期  
└── 其他回答 → 默认设为今天
    ↓
转到离店日期询问
3. 离店日期收集 
text
机器人: "Okay. You want to check in [日期]. And when would you like to check out?"

用户回答:
├── tomorrow → 住1晚
├── 具体星期几 → 设置对应日期
├── 2 days/3 days → 住2-3晚
└── 其他回答 → 默认明天离店
    ↓
转到住客人数询问
4. 住客人数收集
text
机器人: "How many people will be staying? You can say 1, 2, 3, or 4 people."

用户回答处理:
├── 明确数字 (1-4) → 设置人数
├── "just me"/"couple" → 智能推断
├── "family"/"group" → 默认4人
└── 无法识别 → 要求重新输入
    ↓
转到房型选择
5. 房型选择
text
根据住客人数提供不同选项:

1人住客:
├── 选项1: Standard Single Room (800 SEK)
└── 选项2: Deluxe Single Room (1200 SEK)

2+人住客:
├── 选项1: Standard Double Room (1500 SEK)  
└── 选项2: Deluxe Double Room (2000 SEK)

用户选择方式:
├── 数字选择 (1/2)
├── 房间名称
└── 无法识别 → 重新询问
    ↓
转到楼层偏好
6. 楼层偏好
text
机器人根据房型智能推荐:
├── 豪华房 → 推荐高楼层(视野好)
└── 标准房 → 说明两种选择

用户偏好:
├── lower/ground → 低楼层
├── higher/upper → 高楼层  
├── view → 高楼层带视野
├── quiet → 安静楼层
└── 其他 → 无特殊偏好
    ↓
转到早餐选择
7. 早餐选择 (AskBreakfast)
text
智能推荐策略:
├── 豪华房 → 强烈推荐早餐
├── 多人入住 → 强调多样性
└── 单人入住 → 描述早餐内容

价格计算: 100 SEK/人
    ↓
用户选择:
├── 是 → 包含早餐
└── 否 → 不含早餐
    ↓
转到最终确认
8. 最终确认 (ConfirmBooking)
text
机器人总结所有信息:
"Standard Single Room, checking in today, checking out tomorrow, for 1 person, no specific floor preference, without breakfast. Total: 800 SEK per night"

用户确认:
├── 确认正确 → 生成预订编号 → 完成
├── 信息有误 → 重新开始预订
├── 要求重复 → 重新说明详情
└── 询问价格 → 说明价格后重新确认

