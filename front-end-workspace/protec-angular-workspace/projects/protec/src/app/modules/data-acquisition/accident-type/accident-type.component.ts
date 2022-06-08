import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-accident-type',
  templateUrl: './accident-type.component.html',
  styleUrls: ['./accident-type.component.scss']
})
export class AccidentTypeComponent implements OnInit {

  buttons = [{
    label: this.translationService.instant('ARBEITSUNFALL'),
    type: 'outline'
  },
  {
    label: this.translationService.instant('FREIZEITUNFALL'),
    type: 'outline'
  },
  {
    label: this.translationService.instant('KRANKHEIT'),
    type: 'outline'
  }]

  constructor(private readonly translationService: TranslateService) { }

  ngOnInit(): void {
  }

}
