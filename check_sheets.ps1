$excel = New-Object -ComObject Excel.Application
$excel.Visible = $false
try {
    $workbook = $excel.Workbooks.Open('C:\Users\admin\Desktop\查询项目二维码异常数据.xls')
    Write-Output "工作表数量: $($workbook.Sheets.Count)"
    foreach ($sheet in $workbook.Sheets) {
        Write-Output "工作表名称: $($sheet.Name)"
    }
    $workbook.Close($false)
} finally {
    $excel.Quit()
    [System.Runtime.Interopservices.Marshal]::ReleaseComObject($excel) | Out-Null
}
