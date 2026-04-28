$excel = New-Object -ComObject Excel.Application
$excel.Visible = $false
try {
    $workbook = $excel.Workbooks.Open('C:\Users\admin\Desktop\查询项目二维码异常数据.xls')
    $sheet = $workbook.Sheets.Item(1)
    $range = $sheet.UsedRange
    $vals = $range.Value2

    $rowCount = $vals.GetUpperBound(0)
    
    $data1 = @{} # price 有值
    $data2 = @{} # price 没值

    for ($i = 2; $i -le $rowCount; $i++) {
        $did = [string]$vals[$i, 1]
        if ([string]::IsNullOrWhiteSpace($did) -or $did -eq "doctor_id") { continue }
        
        $sum = $vals[$i, 2]
        $price = $vals[$i, 3]

        if ($price -ne $null -and [string]$price -ne "") {
            # 第一个结果集 (price 有值)
            $data1[$did] = $sum
        } else {
            # 第二个结果集 (price 没值)
            $data2[$did] = $sum
        }
    }

    Write-Output "第一个结果集条数: $($data1.Count)"
    Write-Output "第二个结果集条数: $($data2.Count)"

    Write-Output "`n--- 异常数据 (doctorId 相同但 sum 不同) ---"
    Write-Output "doctorId`t`t`t结果集1(Sum)`t结果集2(Sum)"
    
    $foundDiff = $false
    foreach ($did in $data1.Keys) {
        if ($data2.ContainsKey($did)) {
            $v1 = $data1[$did]
            $v2 = $data2[$did]
            
            # 使用近似比较以防浮点数问题，或者直接转为字符串比较
            if ([string]$v1 -ne [string]$v2) {
                Write-Output "$did`t$v1`t`t$v2"
                $foundDiff = $true
            }
        }
    }

    if (-not $foundDiff) {
        Write-Output "未发现 doctorId 相同但 sum 不同的数据。"
    }

    $workbook.Close($false)
} finally {
    $excel.Quit()
    [System.Runtime.Interopservices.Marshal]::ReleaseComObject($excel) | Out-Null
}
