package fxg

import java.awt.Font
import java.text.AttributedString
import java.awt.font.TextAttribute
import java.awt.font.TextLayout

/**
 * Created by IntelliJ IDEA.
 * User: hansolo
 * Date: 28.08.11
 * Time: 08:36
 * To change this template use File | Settings | File Templates.
 */
class FxgRichText extends FxgShape{
    FxgShapeType type = FxgShapeType.TEXT
    double x
    double y
    double fontSize
    AttributedString string
    Font font
    boolean underline
    boolean italic
    boolean bold
    boolean lineThrough
    String text
    String fontFamily

    AttributedString getAttributedString() {
        AttributedString string = new AttributedString(text)
        string.addAttribute(TextAttribute.FONT, fontFamily)
        string.addAttribute(TextAttribute.SIZE, (float) fontSize)
        string.addAttribute(TextAttribute.FONT, font)
        if (bold){
            string.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD)
        }
        if (underline) {
            string.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON)
        }
        if (lineThrough) {
            string.addAttribute(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON)
        }
        return string
    }

    String translateTo(final Language LANGUAGE) {
        StringBuilder code = new StringBuilder()
        String name = "${layerName}_${shapeName}"
        switch (LANGUAGE) {
            case Language.JAVA:
                StringBuilder style = new StringBuilder();
                style.append(font.bold ? "Font.BOLD" : "Font.PLAIN")
                style.append(font.italic ? " | Font.ITALIC" : "")
                code.append("Font ${name}_Font = new Font(\"${font.family}\", ${style.toString()}, (int)(${font.size2D / referenceWidth} * IMAGE_WIDTH));\n")
                code.append("final AttributedString ${name} = new AttributedString(\"$text\");\n")
                code.append("${name}.addAttribute(TextAttribute.FONT, \"${fontFamily}\");\n")
                code.append("${name}.addAttribute(TextAttribute.SIZE, (float)(${font.size2D / referenceWidth} * IMAGE_WIDTH));\n")
                code.append("${name}.addAttribute(TextAttribute.FONT, ${name}_Font);\n")
                if (bold){
                    code.append("${name}.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);\n")
                }
                if (underline) {
                    code.append("${name}.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);\n")
                }
                if (lineThrough) {
                    code.append("${name}.addAttribute(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);\n")
                }
                if (fill.type != null) {
                    appendJavaPaint(code)
                }
                code.append("G2.setFont(${name}_Font);\n")
                code.append("float ${name}_offsetY = (float)(${y / referenceHeight} * IMAGE_HEIGHT) - (new TextLayout(\"$text\", G2.getFont(), G2.getFontRenderContext()).getDescent());\n")
                code.append("G2.drawString(${name}.getIterator(), (float)(${x / referenceWidth} * IMAGE_WIDTH), ${name}_offsetY);\n")
                code.append("\n")
                return code.toString()

            case Language.JAVAFX:
                code.append("Font ${name}_Font = new Font(\"${font.family}\", ${font.size2D / referenceWidth} * IMAGE_WIDTH);\n")
                String fontWeight = (font.bold ? "FontWeight.BOLD" : "FontWeight.PLAIN")
                String fontPosture = (font.italic ? "FontPosture.ITALIC" : "FontPosture.REGULAR")
                code.append("${name}_Font.font(\"${font.family}\", ${fontWeight}, ${fontPosture}, ${font.size2D / referenceWidth} * IMAGE_WIDTH);\n")
                code.append("Text ${name} = new Text();\n")
                code.append("${name}.setText(\"${text}\");\n")
                code.append("${name}.setFont(${name}_Font);\n")
                code.append("${name}.setX(${x / referenceWidth} * IMAGE_WIDTH);\n")
                code.append("${name}.setY(${y / referenceHeight} * IMAGE_HEIGHT);\n")
                code.append(lineThrough ? "${name}.setStrikeThrough(true);\n" : "\n")
                code.append(underline ? "${name}.setUnderline(true);\n" : "\n")
                code.append("\n")
                return code.toString()

            case Language.GWT:
                return "GWT"

            case Language.CANVAS:
                return "CANVAS"

            default:
                return "NOT SUPPORTED"
        }
    }
}
