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
    type: 'outline',
    link: '/data-acquisition/additional-insurance'
  },
  {
    label: this.translationService.instant('FREIZEITUNFALL'),
    type: 'outline',
    link: '/data-acquisition/additional-insurance'
  },
  {
    label: this.translationService.instant('KRANKHEIT'),
    type: 'outline',
    link: '/data-acquisition/additional-insurance'
  }]

  pageNav = {
    navegation: {
      type: 'navegation-buttons',
      navegations: [
        {
          label: this.translationService.instant('zurück'),
          link: '/data-acquisition/accident-intro'
        }
      ]
    }
  }

  showErrorMsg = true;
  textErrorMsg = 'Lorem ipsum dolor sit amet';

  constructor(private readonly translationService: TranslateService) { }

  ngOnInit(): void {
  }

}
