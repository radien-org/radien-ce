import { Component, OnInit } from '@angular/core';
import { TranslationService } from '../../../shared/services/translation/translation.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {

  constructor(private readonly translationService: TranslationService) { }

  ngOnInit(): void {
    this.translationService.setLocale('de');
  }

}
