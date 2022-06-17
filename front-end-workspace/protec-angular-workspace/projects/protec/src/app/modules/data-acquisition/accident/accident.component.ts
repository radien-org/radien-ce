import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-accident',
  templateUrl: './accident.component.html',
  styleUrls: ['./accident.component.scss']
})
export class AccidentComponent implements OnInit {

  pageNav = {
    home: {
      label: this.translationService.instant('HOME'),
      type: 'anchor-button-home'
    },
    back: {
      label: this.translationService.instant('BACK'),
      type: 'footer-nav'
    }
  }

  constructor(private readonly translationService: TranslateService) { }

  ngOnInit(): void {
  }

}
