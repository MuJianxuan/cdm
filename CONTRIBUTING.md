# 贡献指南 🤝

感谢您对CDM (Code Design Mode) 项目的关注！我们欢迎所有形式的贡献，包括代码、文档、测试、问题报告和功能建议。

## 🚀 快速开始

### 开发环境要求
- **Java**: 21 或更高版本（需要启用预览特性）
- **Maven**: 3.6 或更高版本
- **Git**: 最新版本
- **IDE**: IntelliJ IDEA 或 Eclipse（推荐IntelliJ IDEA）

### 项目结构
```
cdm/
├── cdm-core/                    # 核心设计模式实现
├── cdm-spring-boot-starter/     # Spring Boot自动配置
├── cdm-spring-boot-example/     # 使用示例和演示
├── docs/                        # 项目文档
├── .github/                     # GitHub配置
├── LICENSE                      # 许可证文件
├── README.md                    # 项目主文档
├── CONTRIBUTING.md              # 贡献指南（本文件）
└── pom.xml                      # 主Maven配置文件
```

## 📋 贡献类型

### 🐛 问题报告
如果您发现了bug或有功能建议，请通过GitHub Issues提交：

1. **搜索现有问题**：先搜索是否已有类似问题
2. **创建新Issue**：使用相应的模板
3. **提供详细信息**：
   - 问题描述
   - 复现步骤
   - 期望行为
   - 实际行为
   - 环境信息（Java版本、操作系统等）

### 💡 功能建议
我们欢迎新功能建议：

1. **描述功能**：清晰说明功能用途
2. **使用场景**：说明该功能的应用场景
3. **实现建议**：如果可能，提供实现思路
4. **优先级**：说明该功能的紧急程度

### 🔧 代码贡献

#### 贡献流程
1. **Fork项目**：点击GitHub上的Fork按钮
2. **克隆项目**：
   ```bash
   git clone https://github.com/your-username/cdm.git
   cd cdm
   ```
3. **添加上游仓库**：
   ```bash
   git remote add upstream https://github.com/MuJianxuan/cdm.git
   ```
4. **创建特性分支**：
   ```bash
   git checkout -b feature/your-feature-name
   ```
5. **进行开发**：遵循代码规范
6. **运行测试**：
   ```bash
   mvn clean test
   ```
7. **提交更改**：
   ```bash
   git add .
   git commit -m "[模块] 简短描述"
   ```
8. **推送到远程**：
   ```bash
   git push origin feature/your-feature-name
   ```
9. **创建Pull Request**：在GitHub上创建PR

#### 代码规范

##### Java代码规范
- **命名规范**：
  - 类名：PascalCase（如：`EventDrivenStateMachine`）
  - 方法名：camelCase（如：`executeStateTransition`）
  - 常量：UPPER_SNAKE_CASE（如：`MAX_RETRY_COUNT`）
- **代码格式**：
  - 使用4个空格缩进（不使用Tab）
  - 行宽不超过120字符
  - 方法长度不超过50行
- **注释规范**：
  - 所有公共方法必须有JavaDoc
  - 复杂逻辑需要行内注释
  - 注释使用中文或英文，保持统一

##### Maven规范
- **依赖管理**：
  - 所有依赖必须在父POM中统一管理
  - 避免使用SNAPSHOT版本（开发除外）
  - 明确声明依赖范围（compile、provided、test等）
- **版本管理**：
  - 使用`${revision}`进行版本统一管理
  - 遵循语义化版本规范（SemVer）

##### Git提交规范
- **提交格式**：`[模块] 简短描述`
- **示例**：
  - `[cdm-core] Add new singleton implementation`
  - `[cdm-spring-boot-starter] Fix autoconfiguration issue`
  - `[docs] Update README with new examples`
- **提交内容**：
  - 保持提交原子性，每个提交只解决一个问题
  - 提交描述要清晰说明变更内容
  - 重大变更需要在提交描述中说明影响

#### 测试要求

##### 单元测试
- **覆盖率要求**：新代码的单元测试覆盖率不低于80%
- **测试框架**：使用JUnit 5
- **断言库**：使用AssertJ或Hamcrest
- **Mock框架**：使用Mockito
- **测试命名**：使用描述性命名，如`shouldReturnSuccessWhenPaymentIsValid()`

##### 集成测试
- **Spring Boot测试**：使用`@SpringBootTest`
- **Web层测试**：使用`@WebMvcTest`
- **数据层测试**：使用`@DataJpaTest`
- **测试数据库**：使用H2内存数据库

##### 测试示例
```java
@Test
@DisplayName("应该成功创建单例实例")
public void shouldCreateSingletonInstanceSuccessfully() {
    // Given
    Singleton<DatabaseConnection> singleton = new DoubleCheckedSingleton<>(() -> 
        new DatabaseConnection("test"));
    
    // When
    DatabaseConnection instance = singleton.getInstance();
    
    // Then
    assertThat(instance).isNotNull();
    assertThat(singleton.isInitialized()).isTrue();
}
```

### 📖 文档贡献

#### 文档类型
1. **API文档**：JavaDoc注释
2. **使用指南**：Markdown格式
3. **教程文档**：步骤详细的教程
4. **架构文档**：设计决策和架构说明

#### 文档规范
- **语言**：中文或英文，保持统一
- **格式**：使用Markdown格式
- **结构**：清晰的标题层级和目录结构
- **代码示例**：提供可运行的代码示例
- **截图**：必要时提供截图说明

#### 文档更新
- **API变更**：及时更新相关文档
- **新功能**：添加相应的使用说明
- **Bug修复**：更新已知问题列表

## 🔍 代码审查

### 审查标准
1. **功能正确性**：代码是否实现了预期功能
2. **代码质量**：是否符合代码规范
3. **测试覆盖**：是否有足够的测试用例
4. **性能影响**：是否会影响系统性能
5. **安全性**：是否存在安全漏洞

### 审查流程
1. **自动检查**：CI/CD流水线自动检查
2. **人工审查**：维护者进行代码审查
3. **反馈修改**：根据审查意见修改
4. **最终确认**：审查通过后合并

## 🏷️ Issue和PR标签

### Issue标签
- `bug`：Bug报告
- `enhancement`：功能增强
- `documentation`：文档相关
- `good first issue`：适合新手
- `help wanted`：需要帮助
- `question`：问题咨询

### PR标签
- `feature`：新功能
- `bugfix`：Bug修复
- `documentation`：文档更新
- `refactor`：代码重构
- `test`：测试相关

## 📊 开发统计

### 贡献者统计
[![Contributors](https://contrib.rocks/image?repo=MuJianxuan/cdm)](https://github.com/MuJianxuan/cdm/graphs/contributors)

### 活动统计
![GitHub commit activity](https://img.shields.io/github/commit-activity/m/MuJianxuan/cdm)
![GitHub issues](https://img.shields.io/github/issues/MuJianxuan/cdm)
![GitHub pull requests](https://img.shields.io/github/issues-pr/MuJianxuan/cdm)

## 🆘 获取帮助

### 常见问题
1. **构建失败**：检查Java和Maven版本
2. **测试失败**：确保所有依赖正确安装
3. **IDE配置**：导入Maven项目，等待依赖下载完成

### 联系方式
- **GitHub Issues**：技术问题和Bug报告
- **GitHub Discussions**：一般性讨论和建议
- **邮件**：项目维护者邮箱（如有）

### 社区资源
- **Wiki**：项目Wiki页面
- **示例代码**：cdm-spring-boot-example模块
- **API文档**：GitHub Pages托管的Javadoc

## 🙏 致谢

感谢所有为CDM项目做出贡献的开发者们！您的每一份贡献都让这个项目变得更好。

### 特别感谢
- **代码贡献者**：提交代码和改进
- **问题报告者**：帮助发现和修复Bug
- **文档贡献者**：完善项目文档
- **测试贡献者**：提高代码质量

---

**让我们一起构建更好的Java设计模式库！** 🚀

<div align="center">
  
[![Star History Chart](https://api.star-history.com/svg?repos=MuJianxuan/cdm&type=Date)](https://star-history.com/#MuJianxuan/cdm&Date)

</div>
