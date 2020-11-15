package es.danielcr86.expenses.table.layout;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class MonthColumnYearColumnCategoryColumnsLayoutConfig {
    int categoryLabelsRowIndex;
    int monthsColumnIndex;
    int yearsColumnIndex;
}
