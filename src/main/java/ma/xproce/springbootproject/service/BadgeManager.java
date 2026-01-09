package ma.xproce.springbootproject.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import ma.xproce.springbootproject.service.dtos.InscriptionDto;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class BadgeManager implements BadgeService {
    // Couleurs de la charte ENSAM
    private static final Color ENSAM_BLUE = new Color(0, 51, 102);
    private static final Color ENSAM_GOLD = new Color(255, 204, 0);

    @Override
    public byte[] genererBadgePdf(InscriptionDto inscription, String nomEvent, String nomUser) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // 1. Configuration du Document A5
            Document document = new Document(PageSize.A5, 20, 20, 20, 20);
            PdfWriter writer = PdfWriter.getInstance(document, out);
            document.open();

            // 2. Cadre décoratif
            PdfContentByte canvas = writer.getDirectContent();
            canvas.setColorStroke(ENSAM_BLUE);
            canvas.setLineWidth(5);
            canvas.rectangle(15, 15, PageSize.A5.getWidth() - 30, PageSize.A5.getHeight() - 30);
            canvas.stroke();

            // --- HEADER ---
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{1, 3});

            // Logo (URL publique pour l'exemple)
            String logoUrl = "https://upload.wikimedia.org/wikipedia/fr/2/24/Logo_ENSAM_Casablanca.png";
            try {
                Image logo = Image.getInstance(logoUrl);
                logo.scaleToFit(60, 60);
                PdfPCell logoCell = new PdfPCell(logo);
                logoCell.setBorder(Rectangle.NO_BORDER);
                logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                headerTable.addCell(logoCell);
            } catch (Exception e) {
                PdfPCell emptyCell = new PdfPCell(new Phrase("ENSAM"));
                emptyCell.setBorder(Rectangle.NO_BORDER);
                headerTable.addCell(emptyCell);
            }

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Font.NORMAL, ENSAM_BLUE);
            Font subTitleFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL, Color.GRAY);

            Paragraph titlePara = new Paragraph("ENSAM CASABLANCA\n", titleFont);
            titlePara.add(new Phrase("Plateforme Événementielle", subTitleFont));

            PdfPCell textCell = new PdfPCell(titlePara);
            textCell.setBorder(Rectangle.NO_BORDER);
            textCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            textCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            headerTable.addCell(textCell);

            document.add(headerTable);

            // --- LIGNE DE SÉPARATION ---
            document.add(new Paragraph("\n"));
            canvas.setColorFill(ENSAM_GOLD);
            canvas.rectangle(20, document.getPageSize().getHeight() - 90, document.getPageSize().getWidth() - 40, 3);
            canvas.fill();
            document.add(new Paragraph("\n"));

            // --- CORPS DU BADGE ---
            Font eventFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, Font.NORMAL, ENSAM_BLUE);
            Paragraph pEvent = new Paragraph(nomEvent.toUpperCase(), eventFont);
            pEvent.setAlignment(Element.ALIGN_CENTER);
            pEvent.setSpacingAfter(20);
            document.add(pEvent);

            // Badge Type
            PdfPTable passTable = new PdfPTable(1);
            passTable.setWidthPercentage(60);
            PdfPCell passCell = new PdfPCell(new Phrase("PASS ACCÈS : " + inscription.getStatutBadge(),
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Font.NORMAL, Color.WHITE)));
            passCell.setBackgroundColor(ENSAM_BLUE);
            passCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            passCell.setPadding(8);
            passCell.setBorderColor(ENSAM_GOLD);
            passCell.setBorderWidth(2);
            passTable.addCell(passCell);
            document.add(passTable);

            document.add(new Paragraph("\n"));

            // Nom Participant
            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.ITALIC, Color.GRAY);
            Paragraph pLabel = new Paragraph("Délivré à :", labelFont);
            pLabel.setAlignment(Element.ALIGN_CENTER);
            document.add(pLabel);

            Font userFont = FontFactory.getFont(FontFactory.COURIER_BOLD, 22, Font.NORMAL, Color.BLACK);
            Paragraph pUser = new Paragraph(nomUser, userFont);
            pUser.setAlignment(Element.ALIGN_CENTER);
            pUser.setSpacingAfter(10);
            document.add(pUser);

            // --- QR CODE (Génération via ZXing) ---
            String qrContent = "ENSAM-ID:" + inscription.getId() + ";" + nomUser;
            try {
                // Appel de la méthode privée pour générer l'image QR
                Image imgQr = generateQrCodeImage(qrContent, 150, 150);
                imgQr.setAlignment(Element.ALIGN_CENTER);
                document.add(imgQr);
            } catch (Exception e) {
                document.add(new Paragraph("[QR Code Error]"));
            }

            // --- FOOTER ---
            document.add(new Paragraph("\n"));
            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL, Color.GRAY);
            String dateGen = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());

            Paragraph footer = new Paragraph("Généré le " + dateGen + " | ID: #" + inscription.getId(), footerFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du badge", e);
        }
    }

    private Image generateQrCodeImage(String text, int width, int height) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);

        return Image.getInstance(pngOutputStream.toByteArray());
    }
}
