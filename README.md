# LatexConverter

## How to use
* This util allows you convert latex strings into images
* You can do this by instantiating a LatexConverter
```java
public class Main {
    public static void main(String[] args) throws IOException {
        LatexConverter converter = new LatexConverter("latexTemp", "C:\\Program Files\\MikTex 2.9\\miktex\\bin\\x64\\pdflatex.exe");
    }
}
```
* You need to pass the directory you wish to store the converted images to and the command/ path of the command that will execute the .tex to pdf conversion
* On windows machines you have to send the path to the pdflatex command and not just the command name because it won't recognize the command
* On linux you can just pass the pdflatex command
* In order to convert strings after creating a LatexConverter instance use the convert method
```java
public class Main {
    public static void main(String[] args) throws IOException {
        LatexConverter converter = new LatexConverter("latexTemp", "C:\\Program Files\\MikTex 2.9\\miktex\\bin\\x64\\pdflatex.exe");
        converter.convert("\\frac{sin(x^n)}{x^2}");
    }
}
```
* This will return the result in the latex temp directory in the src folder of the repository