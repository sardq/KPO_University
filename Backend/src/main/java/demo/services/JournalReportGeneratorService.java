package demo.services;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import demo.dto.JournalReportDto;
import demo.exceptions.GenerateException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JournalReportGeneratorService {

    public byte[] generate(JournalReportDto dto) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        Document document = new Document(PageSize.A4);
        try {
            PdfWriter.getInstance(document, out);
            document.open();

            BaseFont bf = BaseFont.createFont("fonts/Roboto-Regular.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font regularFont = new Font(bf, 12);
            Font boldFont = new Font(bf, 12, Font.BOLD);

            Paragraph title = new Paragraph("Журнал успеваемости", boldFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);

            Paragraph info = new Paragraph(
                    "Дисциплина: " + dto.getDisciplineName() +
                            "\nГруппа: " + dto.getGroupName() +
                            "\nПреподаватель: " + dto.getTeacherName(),
                    regularFont);
            info.setSpacingAfter(20f);
            document.add(info);

            int columns = dto.getLessonDates().size() + 2;
            PdfPTable table = new PdfPTable(columns);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(20f);

            table.addCell(new PdfPCell(new Phrase("Студент", boldFont)));
            for (var date : dto.getLessonDates()) {
                table.addCell(new PdfPCell(new Phrase(date.format(dateTimeFormatter), boldFont)));
            }
            table.addCell(new PdfPCell(new Phrase("Средняя", boldFont)));

            for (var row : dto.getStudents()) {
                table.addCell(new PdfPCell(new Phrase(row.getStudentName(), regularFont)));
                for (String grade : row.getGrades()) {
                    table.addCell(new PdfPCell(new Phrase(grade, regularFont)));
                }
                table.addCell(new PdfPCell(new Phrase(String.format("%.2f", row.getAverage()), regularFont)));
            }

            document.add(table);

            Paragraph groupAvg = new Paragraph(
                    "Средний балл по группе: " + String.format("%.2f", dto.getGroupAverage()),
                    regularFont);
            groupAvg.setSpacingAfter(30f);
            document.add(groupAvg);

            Paragraph signature = new Paragraph(
                    "Преподаватель: ___________________\n" +
                            "Дата: " + java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    regularFont);
            signature.setAlignment(Element.ALIGN_RIGHT);
            document.add(signature);

        } catch (Exception e) {
            throw new GenerateException("Ошибка генерации PDF", e);
        } finally {
            document.close();
        }

        return out.toByteArray();
    }
}
