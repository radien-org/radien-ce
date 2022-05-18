import { DatePipe, DecimalPipe } from '@angular/common';
import { Pipe, PipeTransform } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { FieldWithFormattingOptions, INumberFormatter, FieldFormat, IDateFormatter } from '../models';
import BooleanFormatter from './classes/booleanFormatter.class';
import DateFormatter from './classes/dateFormatter.class';
import NumberFormatter from './classes/numberFormatter.class';

@Pipe({
  name: 'fieldFormatter',
})
export class FieldFormatterPipe implements PipeTransform {
  constructor(
    private readonly translate: TranslateService,
    private readonly decimalPipe: DecimalPipe,
    private readonly datePipe: DatePipe
  ) {}

  transform(field: FieldFormat): string | number {
    const value = (field as FieldWithFormattingOptions)?.value || field;

    if (value === null) {
      return this.translate.instant('generic.noDataFound');
    }

    switch (typeof value) {
      case 'string':
      case 'object':
        if (value instanceof Date || (field as IDateFormatter).dateString) {
          return new DateFormatter(field as IDateFormatter, this.datePipe).formattedValue;
        } else {
          return value as string;
        }
      case 'boolean':
        return new BooleanFormatter(value).formattedValue;
      case 'number':
        return new NumberFormatter(
          {
            ...(field as INumberFormatter),
            value: (field as INumberFormatter).value || (field as number),
          },
          this.decimalPipe
        ).formattedValue;
      default:
        return value as string | number;
    }
  }
}
