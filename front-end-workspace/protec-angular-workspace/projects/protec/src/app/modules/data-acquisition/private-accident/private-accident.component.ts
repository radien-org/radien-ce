import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { StorageService } from '../../../shared/services/storage/storage.service';
import { LOCAL } from '../../../shared/services/storage/local.enum';

@Component({
  selector: 'app-private-accident',
  templateUrl: './private-accident.component.html',
  styleUrls: ['./private-accident.component.scss']
})
export class PrivateAccidentComponent implements OnInit {

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

  accidentType: string = '';

  option_class = 'hide';

  constructor(private readonly translationService: TranslateService, private readonly storageService: StorageService) {}

  ngOnInit(): void {
    this.accidentType = this.storageService.getItem(LOCAL.ACCIDENT_TYPE);
    this.verifyAccidentType();
  }

  public showOptions(){
    this.option_class = 'show-flex'
  }
  
  public hideOptions(){
    this.option_class = 'hide'
  }

  verifyAccidentType() {
    if(this.accidentType === 'recreational-accident') {
      this.pageNav.navegation.navegations.push(
        {
          label: this.translationService.instant('weiter'),
          link: '/data-acquisition/full-body'
        }
      );
    } else if(this.accidentType === 'disease') {
      this.pageNav.navegation.navegations.push(
        {
          label: this.translationService.instant('weiter'),
          link: '/data-acquisition/illness-diagnostic'
        }
      );
    }
  }
}
