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
  selectedOptions: Set<{name: string, code: string}>  = new Set<any> ();
  shownOptions : any[] = [];

  @Output() public handleOpen = new EventEmitter();
  @Output() public handleChoices = new EventEmitter();

  constructor() {
  }


  ngOnInit(): void {
    this.isToggled = false;
    this.selectedOptions = this.selected;
    if (this.data.isToggled) {
      this.shownOptions = [...this.data.options]
    } else {
      this.shownOptions = Array.from(this.selectedOptions)
    }
  }

  handleToggle () : void {
      this.isToggled = !this.isToggled;
      if (this.isToggled) {
        this.handleOpen.emit(this.data.id);
        this.shownOptions = [...this.data.options]
      } else {
          this.toggleOff();
      }
  }

  toggleOff (): void {
    this.isToggled = false;
    this.shownOptions = Array.from(this.selectedOptions)
  }

  handleOptionSelect(option: {name: string, code: string}): void {
    if (this.selectedOptions.has(option)) {
      console.log(this.selectedOptions)
      this.selectedOptions.delete(option)
    } else {
      this.selectedOptions.add(option)
    }
    this.handleChoices.emit(this.selectedOptions);
  }

  isOptionSelected (option: {name: string, code: string}) : boolean {
    return this.selectedOptions.has(option);
  }

}
