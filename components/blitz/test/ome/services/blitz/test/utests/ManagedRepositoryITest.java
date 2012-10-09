package ome.services.blitz.test.utests;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;
import ome.services.blitz.fire.Registry;
import ome.services.blitz.repo.ManagedRepositoryI;
import ome.services.util.Executor;
import ome.system.Principal;

import omero.grid.Import;
import omero.util.TempFileManager;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ManagedRepositoryITest extends MockObjectTestCase {

    File tmpDir;
    TestManagedRepositoryI tmri;
    Registry reg;

    public class TestManagedRepositoryI extends ManagedRepositoryI {

        public TestManagedRepositoryI(String template, Executor executor,
                Principal principal, Registry reg) throws Exception {
            super(template, executor, principal, reg);
        }

        @Override
        public Import suggestOnConflict(String trueRoot, String relPath,
                String basePath, List<String> paths) {
            return super.suggestOnConflict(trueRoot, relPath, basePath, paths);
        }

        @Override
        public String commonRoot(List<String> paths) {
            return super.commonRoot(paths);
        }

    }

    @BeforeClass
    public void setup() throws Exception {
        this.tmpDir = TempFileManager.create_path("repo", "test", true);
        Mock mockReg = mock(Registry.class);
        this.reg = (Registry) mockReg.proxy();
        this.tmri = new TestManagedRepositoryI("/%year%/%month%/%day%", null,
                null, this.reg);
    }

    @Test
    public void testSuggestOnConflictPassesWithNonconflictingPaths() {
        new File(this.tmpDir, "/my/path");
        String expectedBasePath = "path";
        String suggestedBasePath = this.tmri.suggestOnConflict(this.tmpDir.getAbsolutePath(),
                null, "/my/path", Arrays.asList("/my/path/foo", "/my/path/bar")).sharedPath;
        Assert.assertEquals(expectedBasePath, suggestedBasePath);
    }

    @Test
    public void testSuggestOnConflictReturnsNewPathOnConflict() {
        new File(this.tmpDir, "/upload");
        String expectedBasePath = "upload-1";
        String suggestedBasePath = this.tmri.suggestOnConflict(this.tmpDir.getAbsolutePath(),
                null, "/upload", Arrays.asList("/upload/foo", "/upload/bar")).sharedPath;
        Assert.assertEquals(expectedBasePath, suggestedBasePath);
    }

    @Test
    public void testSuggestOnConflictReturnsBasnePathWithEmptyPathsList() {
        String expectedBasePath = "upload";
        String suggestedBasePath = this.tmri.suggestOnConflict(this.tmpDir.getAbsolutePath(),
                null, "/upload", new ArrayList<String> ()).sharedPath;
        Assert.assertEquals(expectedBasePath, suggestedBasePath);
    }

    @Test
    public void testCommonRootReturns() {
        String expectedCommonRoot = "/home";
        String actualCommonRoot = this.tmri.commonRoot(Arrays.asList("/home/bob/myStuff",
                "/home/alice/myOtherStuff"));
        Assert.assertEquals(expectedCommonRoot, actualCommonRoot);
    }

    @Test
    public void testCommonRootReturnsTopLevelWithUncommonPaths() {
        String expectedCommonRoot = "/";
        String actualCommonRoot = this.tmri.commonRoot(Arrays.asList("/home/bob/myStuff",
                "/data/alice"));
        Assert.assertEquals(expectedCommonRoot, actualCommonRoot);
    }

}
