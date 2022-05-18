import { DatePipe } from '@angular/common';
import { DateFormatterOptions, FormatterInterface, IDateFormatter } from '../../models';

export default class DateFormatter implements IDateFormatter, FormatterInterface {
  public formattedValue: string;
  readonly value: string | Date;
  readonly dateString: boolean;
  readonly options: DateFormatterOptions;

  constructor(field: IDateFormatter, public readonly datePipe: DatePipe) {
    this.value = field.value;
    this.options = field.options;
    this.dateString = field.dateString;
    this.setFormattedValue();
  }

  setFormattedValue() {
    this.formattedValue = this.datePipe.transform(this.value, this.options?.format || 'dd-MM-yyyy');
  }
}
