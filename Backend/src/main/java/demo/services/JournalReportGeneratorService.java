package demo.services;

import java.io.ByteArrayOutputStream;

import org.springframework.stereotype.Service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import demo.dto.JournalReportDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JournalReportGeneratorService {

    public byte[] generate(JournalReportDto dto) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Document document = new Document(PageSize.A4);
        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Paragraph title = new Paragraph("Журнал успеваемости",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20));
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph(
                    "Дисциплина: " + dto.getDisciplineName() +
                            "\nГруппа: " + dto.getGroupName() +
                            "\nПреподаватель: " + dto.getTeacherName(),
                    FontFactory.getFont(FontFactory.HELVETICA, 12)));

            int columns = dto.getLessonDates().size() + 2;
            PdfPTable table = new PdfPTable(columns);
            table.setWidthPercentage(100);

            table.addCell("Студент");
            for (var date : dto.getLessonDates()) {
                table.addCell(date.toString());
            }
            table.addCell("Средняя");

            for (var row : dto.getStudents()) {
                table.addCell(row.getStudentName());
                for (String grade : row.getGrades()) {
                    table.addCell(grade);
                }
                table.addCell(String.format("%.2f", row.getAverage()));
            }

            document.add(table);

            document.add(new Paragraph(
                    "\nСредний балл по группе: " +
                            String.format("%.2f", dto.getGroupAverage())));

        } catch (Exception e) {
            throw new RuntimeException("Ошибка генерации PDF", e);
        } finally {
            document.close();
        }

        return out.toByteArray();
    }
}
