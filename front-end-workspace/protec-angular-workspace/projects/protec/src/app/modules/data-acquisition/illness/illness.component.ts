import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-illness',
  templateUrl: './illness.component.html',
  styleUrls: ['./illness.component.scss']
})
export class IllnessComponent implements OnInit {

  pageNav = {
    navegation: {
      type: 'navegation-buttons',
      navegations: [
        {
          label: this.translationService.instant('zurück'),
          link: '/data-acquisition/details-intro'
        },
        {
          label: this.translationService.instant('weiter'),
          link: '/data-acquisition/full-body'
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
