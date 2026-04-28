$excel = New-Object -ComObject Excel.Application
$excel.Visible = $false
try {
    $workbook = $excel.Workbooks.Open('C:\Users\admin\Desktop\查询项目二维码异常数据.xls')
    $sheet = $workbook.Sheets.Item(1)
    $range = $sheet.UsedRange
    $vals = $range.Value2 # 获取所有数值，减少 COM 调用次数

    $rowCount = $vals.GetUpperBound(0)
    $colCount = $vals.GetUpperBound(1)

    $sets = @()
    for ($i = 1; $i -le $rowCount; $i++) {
        for ($j = 1; $j -le $colCount; $j++) {
            if ($vals[$i, $j] -eq "doctor_id") {
                $sets += @{ row = $i; col = $j }
            }
        }
    }

    Write-Output "找到 $($sets.Count) 个结果集。"

    if ($sets.Count -ge 2) {
        $data1 = @{}
        $data2 = @{}

        # 解析第一个结果集
        $s1 = $sets[0]
        for ($i = $s1.row + 1; $i -le $rowCount; $i++) {
            $did = [string]$vals[$i, $s1.col]
            if ($did -eq "" -or $did -eq "doctor_id") { break }
            $sum = $vals[$i, $s1.col + 1]
            $data1[$did] = $sum
        }

        # 解析第二个结果集
        $s2 = $sets[1]
        for ($i = $s2.row + 1; $i -le $rowCount; $i++) {
            $did = [string]$vals[$i, $s2.col]
            if ($did -eq "" -or $did -eq "doctor_id") { break }
            $sum = $vals[$i, $s2.col + 1]
            $data2[$did] = $sum
        }

        Write-Output "结果集 1 包含 $($data1.Count) 条数据。"
        Write-Output "结果集 2 包含 $($data2.Count) 条数据。"

        Write-Output "`n--- 差异数据 (doctorId 相同但 sum 不同) ---"
        Write-Output "doctorId`t`tSum1`tSum2"
        foreach ($did in $data1.Keys) {
            if ($data2.ContainsKey($did)) {
                $v1 = $data1[$did]
                $v2 = $data2[$did]
                if ($v1 -ne $v2) {
                    Write-Output "$did`t$v1`t$v2"
                }
            }
        }
    } else {
        Write-Output "未能识别到两个不同的结果集，请检查 Excel 格式。"
    }

    $workbook.Close($false)
} finally {
    $excel.Quit()
    [System.Runtime.Interopservices.Marshal]::ReleaseComObject($excel) | Out-Null
}
