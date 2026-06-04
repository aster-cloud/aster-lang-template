# aster-lang-template

[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)

> 15 分钟把 Aster Lang 翻译到你的母语。

## 谁需要这个 repo？

[Aster Lang](https://aster-lang.cloud) 是一个让业务专家用**母语**写策略的 Policy-as-Code 平台。
如果它还没支持你的语种，这个 repo 是你贡献新 lexicon 的起点——无需改 Java 编译器，只需翻译 JSON。

## 15 分钟教程

### 1. Fork & rename（2 分钟）

- Fork 此 repo 到你的 GitHub
- 重命名为 `aster-lang-<lang>-<region>`，例如：
  - `aster-lang-ja`（日语）
  - `aster-lang-fr-ca`（加拿大法语）
  - `aster-lang-ar`（阿拉伯语）
- 用 IDE 全局 rename：
  - `template` → 你的 lang code（例 `ja`）
  - `TEMPLATE` → 大写形式（例 `JA`）
  - `XX-XX` → IETF BCP 47 ID（例 `ja-JP`）

### 2. 翻译 lexicon JSON（10 分钟）

打开 `src/main/resources/lexicons/<lang>-<region>.json`，把每个 `TODO_TRANSLATE_*` value 翻译成你的母语。

**翻译原则**：
- ✅ 保留**行业术语**而非通俗词
  - 例：`Module` 译为 `モジュール` 而非 `組`
- ✅ keyword 之间**不重复**（同一 lexicon 内不同 key 不映射到同字符串）
- ❌ 不使用 Aster 语法**保留字符**：`[](),.;:=`
- ❌ 不在 keyword 中包含数字开头字符
- ✅ 多词 keyword 之间用空格分隔（不用下划线）

参考样本：
- [aster-lang-en/lexicons/en-US.json](https://github.com/aster-cloud/aster-lang-en/blob/main/src/main/resources/lexicons/en-US.json)
- [aster-lang-zh/lexicons/zh-CN.json](https://github.com/aster-cloud/aster-lang-zh/blob/main/src/main/resources/lexicons/zh-CN.json)
- [aster-lang-de/lexicons/de-DE.json](https://github.com/aster-cloud/aster-lang-de/blob/main/src/main/resources/lexicons/de-DE.json)

### 3. 运行 validator（1 分钟）

```bash
./gradlew validateLexicon
```

- 失败 → 按报错提示修复（缺少 keyword / 保留字符 / 重复值 / 非法 meta.id 等）
- 通过 → 进入下一步

### 4. 运行测试（1 分钟）

```bash
./gradlew test
```

测试会用你的 lexicon 跑黄金 sample policy，期望与 en-US lexicon 输出相同的 Core IR。

### 5. 提交收编申请（1 分钟）

你的 fork 已经是一个**可独立运行的社区维护语言包**（SPI 自动发现，无需 Aster 介入即可
本地/自有部署加载——见下文"技术细节 → SPI ABI 兼容性"）。如果你希望它被**官方收编**进
[`aster-lang-locales`](https://github.com/aster-cloud/aster-lang-locales)（走"官方背书"路径）：

- 在 [aster-lang-locales Discussions / Issues](https://github.com/aster-cloud/aster-lang-locales/issues)
  发起收编申请，附上你 fork 的仓库链接
- 申请用自动模板填充：lang / region / direction / vocabulary 列表 / `validateLexicon` 通过截图
- Aster reviewer **24h** 内首次回复；准入流程见
  [aster-lang-locales README 的"官方收编（Adoption）准入流程"](https://github.com/aster-cloud/aster-lang-locales#官方收编adoption准入流程)

> 不想走官方收编也完全可以：保持"社区维护"路径，用你自己的 maven 坐标发布，
> 在 [docs/community/lexicons](https://aster-lang.dev/community/lexicons) 登记即可被其他用户发现。

## 三条贡献路径

| 路径 | 控制 | 落点 | Aster 介入 | 适用 |
|---|---|---|---|---|
| **官方 lexicon** | Aster team 直接维护 | [`aster-lang-locales`](https://github.com/aster-cloud/aster-lang-locales) 的一个 module | 100% | en/zh/de（核心市场） |
| **官方背书 lexicon** | Community 开发 → Aster review → **晋升**为 `aster-lang-locales` 的新 module | 晋升后进 `aster-lang-locales`（晋升前留在你 fork 的模板 repo） | Review + 安全审计 + maven 发布 | 主流语种（ja/fr/es/...） |
| **社区维护 lexicon** | Community 自有 org + 自有 maven coord | 你自己的 repo | 仅 [docs/community/lexicons](https://aster-lang.dev/community/lexicons) 收录 | 长尾语种 / 行业 dialect |

> **关于"官方背书"的落点**：所有 Aster 官方维护的语言包都集中在单一仓库
> [`aster-lang-locales`](https://github.com/aster-cloud/aster-lang-locales)（一语言一 module，不再一语言一 repo）。
> 走"官方背书"路径的语言通过本模板自助开发 + 自测，评审通过后由 Aster team
> **收编**为 `aster-lang-locales` 的新 module。收编准入流程见该仓库 README 的
> "官方收编（Adoption）准入流程"一节。

## 贡献激励

- ✅ **Apache 2.0 license**——你保留贡献者署名权
- ✅ **Aster Language Steward** 标签（合并 ≥ 2 lexicon 或维护 1 lexicon ≥ 12 个月）
- ✅ **¥3,000/年 platform credit**（Steward 限定）
- ✅ 公开 [contributor 名录](https://aster-lang.dev/community/contributors)
- ✅ 优先参与新 SPI ABI 设计讨论

详见 [CONTRIBUTING.md](CONTRIBUTING.md)。

## 技术细节

### 目录结构

```
aster-lang-<lang-region>/
├── LICENSE                       # Apache 2.0
├── NOTICE
├── README.md
├── CONTRIBUTING.md
├── build.gradle.kts              # 依赖 aster-lang-core
├── src/main/
│   ├── java/aster/lang/<lang>/
│   │   └── <Lang>Plugin.java     # SPI 实现（不需要改）
│   └── resources/
│       ├── META-INF/services/
│       │   └── aster.core.lexicon.LexiconPlugin  # SPI 注册（不需要改）
│       ├── lexicons/
│       │   └── <lang>-<region>.json              # 你翻译这个
│       └── overlays/
│           └── lsp-ui-texts.json                 # 可选：LSP UI 翻译
└── src/test/java/aster/lang/<lang>/
    └── <Lang>PluginTest.java
```

### Lexicon JSON 结构

```jsonc
{
  "meta": {
    "id": "ja-JP",                  // IETF BCP 47
    "name": "日本語",                // 该语种自身的名字
    "direction": "LTR"              // LTR 或 RTL
  },
  "keywords": {
    "MODULE_DECL": "モジュール",
    "IMPORT": "使用",
    // ... 所有 keys 必须与 en-US.json 一一对应
  },
  "punctuation": {
    "listSeparator": ",",
    "rangeSeparator": "..",
    "decimalSeparator": "."
  }
}
```

### SPI ABI 兼容性

当前 SPI ABI = **v1.0**，承诺至少保证 18 个月不变更（直到 2027-12-01）。
Breaking change 会提前 6 个月通告 + 新旧 ABI 共存一个版本周期。

## 问题反馈

- [GitHub Discussions](https://github.com/aster-cloud/aster-lang-core/discussions)
- [aster-lang.dev/community](https://aster-lang.dev/community)

## License

Apache 2.0 — see [LICENSE](LICENSE).
