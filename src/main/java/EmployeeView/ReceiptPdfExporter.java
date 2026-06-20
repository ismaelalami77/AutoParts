package EmployeeView;

import Login.User;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ReceiptPdfExporter {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final int PAGE_WIDTH = 612;
    private static final int PAGE_HEIGHT = 842;
    private static final int LEFT = 64;
    private static final int RIGHT = 548;

    public static void export(File file, ReceiptSale sale, List<ReceiptItem> items, User employee) throws Exception {
        StringBuilder content = new StringBuilder();

        drawCenteredText(content, 792, 24, "F2", "AutoParts");
        drawCenteredText(content, 768, 11, "F1", "Auto Parts Management System");
        drawLine(content, LEFT, 746, RIGHT, 746);

        drawText(content, LEFT, 718, 12, "F2", "Receipt #" + sale.getOrderId());
        drawText(content, 380, 718, 11, "F1", "Date: " + formatDate(sale));
        drawText(content, LEFT, 696, 11, "F1", "Employee: " + valueOrEmpty(employee.getFullName()));
        drawText(content, LEFT, 676, 11, "F1", "Customer: " + valueOrEmpty(sale.getCustomerName()));
        drawText(content, 380, 676, 11, "F1", "Phone: " + valueOrEmpty(sale.getCustomerPhone()));

        drawLine(content, LEFT, 648, RIGHT, 648);
        drawText(content, LEFT, 626, 10, "F2", "Item");
        drawText(content, 338, 626, 10, "F2", "Qty");
        drawText(content, 402, 626, 10, "F2", "Price");
        drawText(content, 488, 626, 10, "F2", "Subtotal");
        drawLine(content, LEFT, 612, RIGHT, 612);

        int y = 590;
        for (ReceiptItem item : items) {
            if (y < 118) {
                drawText(content, LEFT, y, 10, "F1", "... more items");
                break;
            }

            drawText(content, LEFT, y, 10, "F1", shorten(valueOrEmpty(item.getProductName()), 34));
            drawText(content, 348, y, 10, "F1", String.valueOf(item.getQuantity()));
            drawRightText(content, 448, y, 10, "F1", String.format("%.2f", item.getUnitPrice()));
            drawRightText(content, RIGHT, y, 10, "F1", String.format("%.2f", item.getSubtotal()));
            y -= 20;
        }

        int totalLineY = Math.max(y - 8, 106);
        drawLine(content, LEFT, totalLineY, RIGHT, totalLineY);
        drawText(content, 374, totalLineY - 28, 13, "F2", "TOTAL");
        drawRightText(content, RIGHT, totalLineY - 28, 13, "F2", String.format("%.2f", sale.getTotalAmount()));

        drawCenteredText(content, 54, 10, "F1", "Thank you for shopping with AutoParts");

        writePdf(file, content.toString());
    }

    private static void writePdf(File file, String content) throws Exception {
        byte[] contentBytes = content.getBytes(StandardCharsets.US_ASCII);

        ArrayList<byte[]> objects = new ArrayList<>();
        objects.add("<< /Type /Catalog /Pages 2 0 R >>".getBytes(StandardCharsets.US_ASCII));
        objects.add("<< /Type /Pages /Kids [3 0 R] /Count 1 >>".getBytes(StandardCharsets.US_ASCII));
        objects.add(("<< /Type /Page /Parent 2 0 R /MediaBox [0 0 " + PAGE_WIDTH + " " + PAGE_HEIGHT + "] "
                + "/Resources << /Font << /F1 4 0 R /F2 5 0 R >> >> /Contents 6 0 R >>")
                .getBytes(StandardCharsets.US_ASCII));
        objects.add("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>".getBytes(StandardCharsets.US_ASCII));
        objects.add("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Bold >>".getBytes(StandardCharsets.US_ASCII));
        objects.add(("<< /Length " + contentBytes.length + " >>\nstream\n"
                + content
                + "endstream").getBytes(StandardCharsets.US_ASCII));

        ByteArrayOutputStream pdf = new ByteArrayOutputStream();
        pdf.write("%PDF-1.4\n".getBytes(StandardCharsets.US_ASCII));

        ArrayList<Integer> offsets = new ArrayList<>();
        for (int i = 0; i < objects.size(); i++) {
            offsets.add(pdf.size());
            pdf.write((i + 1 + " 0 obj\n").getBytes(StandardCharsets.US_ASCII));
            pdf.write(objects.get(i));
            pdf.write("\nendobj\n".getBytes(StandardCharsets.US_ASCII));
        }

        int xrefOffset = pdf.size();
        pdf.write(("xref\n0 " + (objects.size() + 1) + "\n").getBytes(StandardCharsets.US_ASCII));
        pdf.write("0000000000 65535 f \n".getBytes(StandardCharsets.US_ASCII));

        for (Integer offset : offsets) {
            pdf.write(String.format("%010d 00000 n \n", offset).getBytes(StandardCharsets.US_ASCII));
        }

        pdf.write(("trailer\n<< /Size " + (objects.size() + 1) + " /Root 1 0 R >>\n").getBytes(StandardCharsets.US_ASCII));
        pdf.write(("startxref\n" + xrefOffset + "\n%%EOF").getBytes(StandardCharsets.US_ASCII));

        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(pdf.toByteArray());
        }
    }

    private static void drawText(StringBuilder content, int x, int y, int size, String font, String text) {
        content.append("BT\n")
                .append("/").append(font).append(" ").append(size).append(" Tf\n")
                .append(x).append(" ").append(y).append(" Td\n")
                .append("(").append(escapePdfText(toPdfSafeText(text))).append(") Tj\n")
                .append("ET\n");
    }

    private static void drawCenteredText(StringBuilder content, int y, int size, String font, String text) {
        int width = estimateTextWidth(text, size);
        drawText(content, Math.max(LEFT, (PAGE_WIDTH - width) / 2), y, size, font, text);
    }

    private static void drawRightText(StringBuilder content, int rightX, int y, int size, String font, String text) {
        drawText(content, rightX - estimateTextWidth(text, size), y, size, font, text);
    }

    private static void drawLine(StringBuilder content, int x1, int y1, int x2, int y2) {
        content.append("0.82 0.88 0.86 RG\n")
                .append("0.8 w\n")
                .append(x1).append(" ").append(y1).append(" m\n")
                .append(x2).append(" ").append(y2).append(" l\n")
                .append("S\n")
                .append("0 0 0 RG\n");
    }

    private static int estimateTextWidth(String text, int fontSize) {
        return (int) Math.round(toPdfSafeText(text).length() * fontSize * 0.52);
    }

    private static String formatDate(ReceiptSale sale) {
        return sale.getOrderDate() == null ? "" : sale.getOrderDate().format(DATE_FORMAT);
    }

    private static String escapePdfText(String text) {
        return text.replace("\\", "\\\\")
                .replace("(", "\\(")
                .replace(")", "\\)");
    }

    private static String toPdfSafeText(String text) {
        return text.replaceAll("[^\\x20-\\x7E]", "?");
    }

    private static String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }

    private static String shorten(String value, int maxLength) {
        if (value.length() <= maxLength) {
            return value;
        }

        return value.substring(0, maxLength - 3) + "...";
    }
}
