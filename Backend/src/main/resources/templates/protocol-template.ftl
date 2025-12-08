<!DOCTYPE html>
<html lang="ru">
<head>
  <meta charset="UTF-8" />
  <title>Журнал успеваемости</title>
  <style>
    @font-face {
      font-family: 'DejaVuSans';
      src: url('file:fonts/DejaVuSans.ttf') format('truetype');
    }

    @font-face {
      font-family: 'Roboto-Bold';
      src: url('file:fonts/Roboto-Bold.ttf') format('truetype');
      font-weight: bold;
    }

    body {
      font-family: 'Roboto', sans-serif;
      margin: 50px;
      line-height: 1.5;
    }
    h1 {
      text-align: center;
      font-size: 24px;
      margin-bottom: 30px;
    }
    .section {
      margin-bottom: 20px;
    }
    .section h2 {
      font-size: 18px;
      margin-bottom: 10px;
    }
    table {
      width: 100%;
      border-collapse: collapse;
      margin-bottom: 20px;
    }
    table th, table td {
      border: 1px solid #ddd;
      padding: 8px;
      text-align: center;
    }
    table th {
      background-color: #f2f2f2;
    }
    .signature {
      margin-top: 50px;
      text-align: right;
    }
  </style>
</head>
<body>

<h1>ЖУРНАЛ УСПЕВАЕМОСТИ</h1>

<div class="section">
  <p><strong>Дисциплина:</strong> ${disciplineName}</p>
  <p><strong>Группа:</strong> ${groupName}</p>
  <p><strong>Преподаватель:</strong> ${teacherName}</p>
</div>

<div class="section">
  <table>
    <thead>
      <tr>
        <th>Студент</th>
        <#list lessonDates as date>
          <th>${date?string("dd.MM.yyyy")}</th>
        </#list>
        <th>Средняя</th>
      </tr>
    </thead>
    <tbody>
      <#list students as student>
        <tr>
          <td>${student.studentName}</td>
          <#list student.grades as grade>
            <td>${grade}</td>
          </#list>
          <td>${student.average?string("0.00")}</td>
        </tr>
      </#list>
    </tbody>
  </table>
</div>

<div class="section">
  <p><strong>Средний балл по группе:</strong> ${groupAverage?string("0.00")}</p>
</div>

<div class="signature">
  <p>Преподаватель: ___________________</p>
  <p>Дата: ${.now?string("dd.MM.yyyy")}</p>
</div>

</body>
</html>
