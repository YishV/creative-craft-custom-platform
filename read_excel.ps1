$excel = New-Object -ComObject Excel.Application
$excel.Visible = $false
$workbook = $excel.Workbooks.Open('C:\Users\admin\Desktop\查询项目二维码异常数据.xls')
$sheet = $workbook.Sheets.Item(1)
for($i=1; $i -le 20; $i++) {
    $row = ""
    for($j=1; $j -le 10; $j++) {
        $cellText = $sheet.Cells.Item($i, $j).Text
        $row += "$cellText`t"
    }
    Write-Output $row
}
$workbook.Close($false)
$excel.Quit()
[System.Runtime.Interopservices.Marshal]::ReleaseComObject($excel) | Out-Null
