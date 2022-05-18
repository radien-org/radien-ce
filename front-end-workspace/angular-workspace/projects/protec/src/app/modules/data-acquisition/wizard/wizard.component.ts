import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-wizard',
  templateUrl: './wizard.component.html',
  styleUrls: ['./wizard.component.scss']
})
export class WizardComponent implements OnInit {

  dataButton = {
    label: this.translationService.instant('buttons.JETZT_STARTEN'),
    type: 'outline'
  }

  constructor(private readonly translationService: TranslateService) { }

  ngOnInit(): void {}

}
