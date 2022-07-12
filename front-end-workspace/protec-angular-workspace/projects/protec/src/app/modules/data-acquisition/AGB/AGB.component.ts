import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'agb',
  templateUrl: './AGB.component.html',
  styleUrls: ['./AGB.component.scss']
})
export class AGBComponent implements OnInit {

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
