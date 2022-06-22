import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-legal-data-policy',
  templateUrl: './legal-data-policy.component.html',
  styleUrls: ['./legal-data-policy.component.scss']
})
export class LegalDataPolicyComponent implements OnInit {

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
