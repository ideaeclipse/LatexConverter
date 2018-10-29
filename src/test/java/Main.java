
import ideaeclipse.latexConverter.LatexConverter;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        new LatexConverter("latexTemp", "C:\\Program Files\\MikTex 2.9\\miktex\\bin\\x64\\pdflatex.exe").convert("\\frac{sin(x^n)}{x^2}");
    }
}
