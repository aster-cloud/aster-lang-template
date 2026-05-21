package aster.lang.template;

import aster.core.canonicalizer.SyntaxTransformer;
import aster.core.identifier.DomainVocabulary;
import aster.core.identifier.VocabularyPlugin;
import aster.core.identifier.VocabularyPluginSupport;
import aster.core.lexicon.DynamicLexicon;
import aster.core.lexicon.Lexicon;
import aster.core.lexicon.LexiconAbiVersion;
import aster.core.lexicon.LexiconPlugin;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * TODO[CONTRIBUTOR]: 重命名为你的语种
 *   - 例 ja:    aster.lang.ja.JaJpPlugin
 *   - 例 fr-CA: aster.lang.fr.FrCaPlugin
 *
 * Implements BOTH SPIs:
 *   - {@link LexiconPlugin}: lexical/keyword + overlay resources
 *   - {@link VocabularyPlugin}: domain vocabularies (industry-specific terms)
 *
 * Reference impl: aster-lang-en/src/main/java/aster/lang/en/EnUsPlugin.java
 */
public final class TemplatePlugin implements LexiconPlugin, VocabularyPlugin {

    @Override
    public Lexicon createLexicon() {
        // TODO[CONTRIBUTOR]: 改资源路径为 你的 lang-region
        String json = loadResource("lexicons/template-XX-XX.json");
        return DynamicLexicon.fromJsonString(json);
    }

    /**
     * R6-M1: 提供静态 metadata 让 preview 零副作用。
     * TODO[CONTRIBUTOR]: 改为你的 BCP-47 locale id（必须与 lexicon JSON 的 id 字段一致）
     */
    @Override
    public Set<String> providedLexiconIds() {
        return Set.of("template-XX-XX");
    }

    /**
     * TODO[CONTRIBUTOR]: 主要领域词汇表（可选；若无可返回 {@code null}）。
     * 推荐为每种语言提供至少一个领域词汇表作为示例（如汽车保险、贷款金融）。
     */
    @Override
    public DomainVocabulary createVocabulary() {
        return VocabularyPluginSupport.loadVocabulary(getClass(), "vocabularies/template-domain.json");
    }

    /**
     * TODO[CONTRIBUTOR]: 额外的领域词汇表（可选）。
     */
    @Override
    public List<DomainVocabulary> getVocabularies() {
        return List.of();
    }

    @Override
    public Map<String, String> getOverlayResources() {
        return Map.of(
            "lspUiTexts", "overlays/lsp-ui-texts.json"
            // TODO[CONTRIBUTOR]: 可选补充 typeInferenceRules / diagnosticMessages
        );
    }

    @Override
    public Map<String, Supplier<SyntaxTransformer>> getTransformers() {
        // 大多数语种无需自定义 transformer
        return Map.of();
    }

    @Override
    public String getAbiVersion() {
        return LexiconAbiVersion.V1.version;
    }

    private String loadResource(String path) {
        try (var is = getClass().getClassLoader().getResourceAsStream(path)) {
            if (is == null) {
                throw new IllegalStateException("Resource not found on classpath: " + path);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load resource: " + path, e);
        }
    }
}
