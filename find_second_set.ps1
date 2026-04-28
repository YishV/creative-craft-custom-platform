$excel = New-Object -ComObject Excel.Application
$excel.Visible = $false
$workbook = $excel.Workbooks.Open('C:\Users\admin\Desktop\查询项目二维码异常数据.xls')
$sheet = $workbook.Sheets.Item(1)

$secondHeaderRow = 0
$rowCount = $sheet.UsedRange.Rows.Count
Write-Output "总行数: $rowCount"

for($i=2; $i -le $rowCount; $i++) {
    $val = $sheet.Cells.Item($i, 1).Text
    if ($val -eq "doctor_id") {
        $secondHeaderRow = $i
        Write-Output "找到第二个结果集，起始行: $secondHeaderRow"
        break
    }
}

if ($secondHeaderRow -eq 0) {
    Write-Output "未在第一列发现第二个结果集。检查其他列..."
    for($j=2; $j -le 10; $j++) {
        $val = $sheet.Cells.Item(1, $j).Text
        if ($val -eq "doctor_id") {
            Write-Output "找到第二个结果集，起始列: $j"
            break
        }
    }
}

$workbook.Close($false)
$excel.Quit()
[System.Runtime.Interopservices.Marshal]::ReleaseComObject($excel) | Out-Null
