$excel = New-Object -ComObject Excel.Application
$excel.Visible = $false
$workbook = $excel.Workbooks.Open('C:\Users\admin\Desktop\查询项目二维码异常数据.xls')
$sheet = $workbook.Sheets.Item(1)

# 搜索第二个结果集（可能在右侧或下方）
Write-Output "--- 扫描前 50 行和前 20 列 ---"
for($i=1; $i -le 50; $i++) {
    $row = ""
    for($j=1; $j -le 20; $j++) {
        $cellText = $sheet.Cells.Item($i, $j).Text
        if ($cellText -ne "") {
            $row += "[$i,$j]:$cellText`t"
        }
    }
    if ($row -ne "") {
        Write-Output $row
    }
}

$workbook.Close($false)
$excel.Quit()
[System.Runtime.Interopservices.Marshal]::ReleaseComObject($excel) | Out-Null
