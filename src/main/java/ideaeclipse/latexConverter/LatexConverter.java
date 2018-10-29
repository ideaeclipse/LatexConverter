package ideaeclipse.latexConverter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;


/**
 * This utility converts latex strings into images
 *
 * @author Ideaeclipse
 */
public class LatexConverter {
    private final String absolutePathToTempDirWithTime;
    private final String pdfLatexDirectory;
    private final String tempTexName = "latexConverter";
    private final boolean isWindows;

    /**
     * @param directoryName     the name of the folder you wish to store the produced images
     * @param pdfLatexDirectory the command / directory of the command that will convert the .tex file to pdf for more info visit 'https://www.latex-project.org/about/'
     */
    public LatexConverter(final String directoryName, final String pdfLatexDirectory) {
        isWindows = System.getProperty("os.name").toLowerCase().contains("windows");
        String absolutePathToTempDir = System.getProperty("user.dir") + (isWindows ? "\\" : "/") + directoryName;
        this.absolutePathToTempDirWithTime = absolutePathToTempDir + (isWindows ? "\\" : "/") + System.currentTimeMillis();
        this.pdfLatexDirectory = pdfLatexDirectory;
        File directory = new File(absolutePathToTempDir);
        if (!directory.exists())
            directory.mkdir();
        File directory2 = new File(this.absolutePathToTempDirWithTime);
        if (!directory2.exists())
            directory2.mkdir();

    }

    /**
     * @param latexString latex string that will be converted
     * @return the directory in which the file is stored in
     * @throws IOException          when files can't be created or accessed
     * @throws InterruptedException if data is tried to be access before completion
     */
    public String convert(final String latexString) throws IOException, InterruptedException {
        String execName = createExecutableFile();
        writeToFile(this.absolutePathToTempDirWithTime + (isWindows ? "\\" : "/") + tempTexName + ".tex", convertLatexString(latexString));
        ProcessBuilder pb = new ProcessBuilder(execName);
        pb.start().waitFor();
        convertToPng();
        deleteAllTempFiles();
        return this.absolutePathToTempDirWithTime + (isWindows ? "\\" : "/") + "picture.png";
    }

    /**
     * @return the path to the executable created
     * @throws IOException if the file can't be created
     */
    private String createExecutableFile() throws IOException {
        String batFileName = "convert";
        String execName = this.absolutePathToTempDirWithTime + (isWindows ? "\\" : "/") + batFileName + (isWindows ? ".bat" : ".sh");
        String builder = "cd " + this.absolutePathToTempDirWithTime + "\n" +
                (isWindows ? "\"" + this.pdfLatexDirectory + "\"" : this.pdfLatexDirectory) + " --shell-escape " + this.tempTexName + ".tex";
        writeToFile(execName, builder);
        if (!isWindows)
            Runtime.getRuntime().exec("chmod u+x " + execName);
        return execName;
    }

    /**
     * @param fileName path to file you wish to write to
     * @param fileData data you wish to write to the specified file
     */
    private void writeToFile(final String fileName, final String fileData) {
        if (createFile(new File(fileName))) {
            FileWriter writer;
            try {
                writer = new FileWriter(fileName, false);
                writer.write(fileData, 0, fileData.length());
                writer.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * @param file file path you wish to created
     * @return if the file gets created
     */
    private boolean createFile(final File file) {
        if (!file.exists()) {
            try {
                return file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * @param equation equation you inputted
     * @return a valid .tex file that can be converted to pdf format
     */
    private String convertLatexString(final String equation) {
        String newLineWithSeparation = System.getProperty("line.separator") + System.getProperty("line.separator");
        String math = "";
        math += "\\documentclass[border=0.50001bp,convert={convertexe={imgconvert},outext=.png}]{standalone}" + newLineWithSeparation;
        math += "\\usepackage{amsfonts}" + newLineWithSeparation;
        math += "\\usepackage{amsmath}" + newLineWithSeparation;
        math += "\\begin{document}" + newLineWithSeparation;
        math += "$\\begin{array}{l}" + newLineWithSeparation;
        math += equation + newLineWithSeparation;
        math += "\\end{array}$" + newLineWithSeparation;
        math += "\\end{document}";
        return math;
    }

    /**
     * converts the generated pdf into a png file
     */
    private void convertToPng() {
        try (final PDDocument document = PDDocument.load(new File(this.absolutePathToTempDirWithTime + (isWindows ? "\\" : "/") + this.tempTexName + ".pdf"))) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            for (int page = 0; page < document.getNumberOfPages(); ++page) {
                BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);
                String fileName = this.absolutePathToTempDirWithTime + (isWindows ? "\\" : "/") + "picture.png";
                ImageIOUtil.writeImage(bim, fileName, 300);
            }
        } catch (IOException e) {
            System.err.println("Exception while trying to create pdf document - " + e);
        }
    }

    /**
     * deletes all files when done except for the image
     */
    private void deleteAllTempFiles() {
        for (File file : (Objects.requireNonNull(new File(this.absolutePathToTempDirWithTime).listFiles()))) {
            if (!file.getName().equals("picture.png"))
                file.delete();
        }
    }
}
