package aster.lang.template;

import aster.core.identifier.DomainVocabulary;
import aster.core.lexicon.Lexicon;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Sanity tests that contributors can run with {@code ./gradlew test} immediately
 * after forking the template — even before any translation has been done.
 *
 * <p>Two layers:
 * <ol>
 *   <li><b>SPI structure tests</b> (always active): the plugin compiles, loads
 *       resources, exposes both LexiconPlugin and VocabularyPlugin contracts.
 *       These pass even with the stock {@code template-XX-XX} placeholder.</li>
 *   <li><b>Translation-readiness tests</b> (always active): the plugin
 *       <em>self-reports</em> whether it still has {@code TODO_TRANSLATE_*} or
 *       {@code XX-XX} placeholders. These intentionally PASS for the template
 *       itself (so the build is green out of the box) but a contributor can
 *       adapt them by changing the assertions in {@code translationReadinessTests()}.
 *       See {@code CONTRIBUTING.md} for the recommended workflow.</li>
 * </ol>
 *
 * <p>This replaces the previous all-{@code @Disabled} suite that forced
 * contributors to read multiple files before knowing whether the build worked.
 */
class TemplatePluginTest {

    @Test
    @DisplayName("Plugin instantiates without throwing")
    void pluginInstantiates() {
        TemplatePlugin plugin = new TemplatePlugin();
        assertThat(plugin).isNotNull();
        assertThat(plugin.getAbiVersion()).startsWith("1.");
    }

    @Test
    @DisplayName("LexiconPlugin contract: createLexicon loads the JSON resource")
    void createLexiconReturnsParsedLexicon() {
        TemplatePlugin plugin = new TemplatePlugin();
        Lexicon lexicon = plugin.createLexicon();
        assertThat(lexicon).isNotNull();
        // Stock template id is "template-XX-XX"; contributors must change it.
        assertThat(lexicon.getId()).isNotBlank();
    }

    @Test
    @DisplayName("LexiconPlugin contract: providedLexiconIds matches createLexicon().getId()")
    void providedIdsAreConsistentWithCreated() {
        TemplatePlugin plugin = new TemplatePlugin();
        Lexicon lexicon = plugin.createLexicon();
        assertThat(plugin.providedLexiconIds()).contains(lexicon.getId());
    }

    @Test
    @DisplayName("LexiconPlugin contract: overlay resources resolve on classpath")
    void overlayResourcesResolve() {
        TemplatePlugin plugin = new TemplatePlugin();
        for (var entry : plugin.getOverlayResources().entrySet()) {
            String path = entry.getValue();
            try (var is = TemplatePlugin.class.getClassLoader().getResourceAsStream(path)) {
                assertThat(is)
                    .as("overlay '%s' must be available at classpath:%s", entry.getKey(), path)
                    .isNotNull();
            } catch (Exception e) {
                throw new AssertionError("Failed to open overlay resource " + path, e);
            }
        }
    }

    @Test
    @DisplayName("VocabularyPlugin contract: createVocabulary loads at least one domain vocabulary")
    void createVocabularyReturnsLoadedFixture() {
        TemplatePlugin plugin = new TemplatePlugin();
        DomainVocabulary primary = plugin.createVocabulary();
        assertThat(primary).isNotNull();
        assertThat(primary.id()).isNotBlank();
    }

    @Test
    @DisplayName("VocabularyPlugin SPI service file is registered")
    void vocabularyServiceFileIsRegistered() {
        try (var is = TemplatePlugin.class.getClassLoader().getResourceAsStream(
            "META-INF/services/aster.core.identifier.VocabularyPlugin")) {
            assertThat(is)
                .as("META-INF/services/aster.core.identifier.VocabularyPlugin must list TemplatePlugin so ServiceLoader can discover it")
                .isNotNull();
        } catch (Exception e) {
            throw new AssertionError("Failed to open service file", e);
        }
    }

    @Test
    @DisplayName("LexiconPlugin SPI service file is registered")
    void lexiconServiceFileIsRegistered() {
        try (var is = TemplatePlugin.class.getClassLoader().getResourceAsStream(
            "META-INF/services/aster.core.lexicon.LexiconPlugin")) {
            assertThat(is)
                .as("META-INF/services/aster.core.lexicon.LexiconPlugin must list TemplatePlugin")
                .isNotNull();
        } catch (Exception e) {
            throw new AssertionError("Failed to open service file", e);
        }
    }

    @Test
    @DisplayName("Translation-readiness probe: lexicon id is still 'template-XX-XX' (expected for stock template)")
    void translationReadinessLexiconId() {
        // This test intentionally documents the current state. When you change
        // the id, this assertion will fail until you update the expected value
        // — that's a feature, not a bug: it forces you to acknowledge the rename.
        TemplatePlugin plugin = new TemplatePlugin();
        assertThat(plugin.createLexicon().getId())
            .as("Once renamed, update this assertion to your locale id (e.g. ja-JP)")
            .isEqualTo("template-XX-XX");
    }
}
