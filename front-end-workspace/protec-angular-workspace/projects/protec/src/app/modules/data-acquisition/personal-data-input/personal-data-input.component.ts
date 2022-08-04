import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-illness',
  templateUrl: './personal-data-input.component.html',//TODO change component name to personal-data-person
  styleUrls: ['./personal-data-input.component.scss']
})
export class PersonalDataInputComponent implements OnInit {

  pageNav = {
    navegation: {
      type: 'navegation-buttons',
      navegations: [
        {
          label: this.translationService.instant('zurück'),
          link: '/data-acquisition/personal-data-contact'
        },
        {
          label: this.translationService.instant('weiter'),
          link: '/data-acquisition/appreciation'
        }
      ]
    }
  }

  constructor(private readonly translationService: TranslateService) { }

  ngOnInit(): void {
  }

  option_class = 'hide';
  public showOptions(){
    this.option_class = 'show-flex'
  }
  public hideOptions(){
    this.option_class = 'hide'
  }
}
