import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-cookie-guide-line',
  templateUrl: './cookie-guide-line.component.html',
  styleUrls: ['./cookie-guide-line.component.scss']
})
export class CookieGuideLineComponent implements OnInit {

  constructor(private readonly translationService: TranslateService) { }

  ngOnInit(): void {
  }

}
