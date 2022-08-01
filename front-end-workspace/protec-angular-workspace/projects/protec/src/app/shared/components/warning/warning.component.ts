import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-warning',
  templateUrl: './warning.component.html',
  styleUrls: ['./warning.component.scss']
})
export class WarningComponent implements OnInit {

  @Input() public text: string = 'Lorem ipsum dolor sit amet';
  @Input() public show: boolean = false;

  constructor() { }

  ngOnInit(): void {
  }

  close(){
    this.show = false;
  }

}
