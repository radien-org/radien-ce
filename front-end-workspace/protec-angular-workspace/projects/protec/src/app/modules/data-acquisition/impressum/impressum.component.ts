import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-impressum',
  templateUrl: './impressum.component.html',
  styleUrls: ['./impressum.component.scss']
})
export class ImpressumComponent implements OnInit {

  pageNav = {
    home: {
      label: this.translationService.instant('HOME'),
      type: 'anchor-button-home',
      link: '/data-acquisition'
    }
  }
  
  constructor(private readonly translationService: TranslateService) { }

  ngOnInit(): void {
  }

}
