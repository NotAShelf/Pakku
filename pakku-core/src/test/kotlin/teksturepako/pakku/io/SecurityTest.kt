package teksturepako.pakku.io

import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isFalse
import strikt.assertions.isTrue
import teksturepako.pakku.PakkuTest
import kotlin.io.path.Path

class SecurityTest : PakkuTest()
{
    private val invalidPaths = listOf(
        "/", "\\", "C:/", "C:\\", "..", "test_path/../", "test_path\\..\\", "/coconut/", "\\coconut\\"
    )
    private val validPaths = listOf(
        "./coconut/", "test_path/coconut/", "coconut", "1.20.x"
    )

    @Test
    fun `test path filter`()
    {
        for (path in invalidPaths)
        {
            expectThat(filterPath(path))
                .get { isErr }
                .isTrue()
        }

        for (path in validPaths)
        {
            expectThat(filterPath(path))
                .get { isOk }
                .isTrue()
        }
    }

    @Test
    fun `absolute filesystem paths are not unsafe solely for being absolute`()
    {
        val absoluteUnderWorkingPath = testPath("mods", "example.jar").toAbsolutePath()

        expectThat(absoluteUnderWorkingPath.hasUnsafePathComponents()).isFalse()
    }

    @Test
    fun `absolute paths with parent segments are unsafe`()
    {
        val withParent = testPath("mods").resolve("..").resolve("outside.txt")

        expectThat(withParent.hasUnsafePathComponents()).isTrue()
    }

    @Test
    fun `relative entry paths still reject absolute roots via filterPath`()
    {
        expectThat(filterPath("/etc/passwd").isErr).isTrue()
        expectThat(filterPath("mods/example.jar").isOk).isTrue()
        expectThat(Path("mods/example.jar").hasUnsafePathComponents()).isFalse()
        expectThat(Path("../escape").hasUnsafePathComponents()).isTrue()
    }

    @Test
    fun `isSafeFileName rejects traversal and separators`()
    {
        expectThat("../../etc/passwd".isSafeFileName()).isFalse()
        expectThat("mods/evil.jar".isSafeFileName()).isFalse()
        expectThat("evil.jar".isSafeFileName()).isTrue()
        expectThat("Greenery-1.12.2-7.0.jar".isSafeFileName()).isTrue()
    }
}
