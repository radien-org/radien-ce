import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-g-button',
  templateUrl: './g-button.component.html',
  styleUrls: ['./g-button.component.scss']
})
export class GButtonComponent implements OnInit {

  @Input() public data = {
    label: 'no-data',
    type: 'default'
  };

  constructor() { }

  ngOnInit(): void {
  }

}
