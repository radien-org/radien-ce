import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { StorageService } from '../../../shared/services/storage/storage.service';
import { LOCAL } from '../../../shared/services/storage/local.enum';

@Component({
  selector: 'app-details-intro',
  templateUrl: './details-intro.component.html',
  styleUrls: ['./details-intro.component.scss']
})
export class DetailsIntroComponent implements OnInit {

  pageNav = {
    navegation: {
      type: 'navegation-buttons',
      navegations: [
        {
          label: this.translationService.instant('zurück'),
          link: '/data-acquisition/accident-date'
        }
      ]
    }
  }

  accidentType: string = '';

  constructor(private readonly translationService: TranslateService, private readonly storageService: StorageService) {}

  ngOnInit(): void {
    this.accidentType = this.storageService.getItem(LOCAL.ACCIDENT_TYPE);
    this.verifyAccidentType();
  }

  verifyAccidentType() {
    if(this.accidentType === 'work-accident') {
      this.pageNav.navegation.navegations.push(
        {
          label: this.translationService.instant('weiter'),
          link: '/data-acquisition/work-accident'
        }
      );
    } else {
      this.pageNav.navegation.navegations.push(
        {
          label: this.translationService.instant('weiter'),
          link: '/data-acquisition/private-accident'
        }
      );
    }
  }

}
