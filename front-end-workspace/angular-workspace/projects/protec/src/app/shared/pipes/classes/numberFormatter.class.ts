import { DecimalPipe } from '@angular/common';
import { FormatterInterface, INumberFormatter, NumberFormatterOptions } from '../../models';

export default class NumberFormatter implements INumberFormatter, FormatterInterface {
  public formattedValue: string | number;
  readonly value: number;
  readonly options: NumberFormatterOptions;

  constructor(field: INumberFormatter, public readonly decimalPipe: DecimalPipe) {
    this.value = field.value;
    this.options = field.options;
    this.setFormattedValue();
  }

  setFormattedValue() {
    let tempValue;
    const digitsInfo = this.options?.digitsInfo || '1.0-9';
    if (this.options?.currency) {
      const symbol = this.options?.currency?.symbol;
      const symbolPosition = this.options?.currency?.symbolPosition || 'left';
      const decimalValue = this.decimalPipe.transform(this.value, digitsInfo);
      tempValue = symbolPosition === 'left' ? `${symbol} ${decimalValue}` : `${decimalValue} ${symbol}`;
    }
    this.formattedValue = tempValue || this.decimalPipe.transform(this.value, digitsInfo);
  }
}
