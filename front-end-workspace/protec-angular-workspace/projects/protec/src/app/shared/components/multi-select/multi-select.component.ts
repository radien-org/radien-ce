import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';


@Component({
  selector: 'app-multi-select',
  templateUrl: './multi-select.component.html',
  styleUrls: ['./multi-select.component.scss']
})
export class MultiSelectComponent implements OnInit {

  @Input() public data: any = {
    id: '',
    title: '',
    options: [],
  }
  @Input() public selected: Set<any> = new Set<any>();
  isToggled : boolean = false;
  selectedOptions: Set<string>  = new Set<string> ();
  shownOptions : any[] = [];

  @Output() public handleOpen = new EventEmitter();
  @Output() public handleChoices = new EventEmitter();

  constructor() {
    this.selectedOptions = this.selected;
  }


  ngOnInit(): void {
    this.isToggled = false;
    this.selectedOptions = this.selected;
    if (this.data.isToggled) {
      this.shownOptions = Array.from(new Set(this.data.options.map((option: { name: any; }) => option.name)))
    } else {
      this.shownOptions = Array.from(this.selectedOptions)
    }
  }

  handleToggle () : void {
      this.isToggled = !this.isToggled;
      if (this.isToggled) {
        this.handleOpen.emit(this.data.id);
        this.shownOptions = Array.from(new Set(this.data.options.map((option: { name: any; }) => option.name)))
      } else {
          this.toggleOff();
      }
  }

  toggleOff (): void {
    this.isToggled = false;
    this.shownOptions = Array.from(this.selectedOptions)
  }

  handleOptionSelect(option: string): void {
    if (this.selectedOptions.has(option)) {
      console.log(this.selectedOptions)
      this.selectedOptions.delete(option)
    } else {
      this.selectedOptions.add(option)
    }
    this.handleChoices.emit(this.selectedOptions);
  }

  isOptionSelected (option: string) : boolean {
    return this.selectedOptions.has(option);
  }

}
