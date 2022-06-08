import { FormatterInterface, IBooleanFormatter } from '../../models';

export default class BooleanFormatter implements IBooleanFormatter, FormatterInterface {
  public formattedValue: 'True' | 'False';
  readonly value: boolean;

  constructor(value: boolean) {
    this.value = value;
    this.setFormattedValue();
  }

  setFormattedValue() {
    this.formattedValue = this.value ? 'True' : 'False';
  }
}