import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { StorageService } from '../../../shared/services/storage/storage.service';
import { LOCAL } from '../../../shared/services/storage/local.enum';

@Component({
  selector: 'app-details-figure',//TODO change this component name to full-body-figure
  templateUrl: './details-figure.component.html',
  styleUrls: ['./details-figure.component.scss']
})
export class DetailsFigureComponent implements OnInit {

  pageNav = {
    navegation: {
      type: 'navegation-buttons',
      navegations: [
        {
          label: this.translationService.instant('ZURÜCK'),
          link: '/'
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
    this.pageNav.navegation.navegations.splice(0, 1);
    if(this.accidentType === 'recreational-accident') {
      this.pageNav.navegation.navegations.push(
        {
          label: this.translationService.instant('ZURÜCK'),
          link: '/data-acquisition/private-accident'
        }
      );
    } else if(this.accidentType === 'work-accident') {
      this.pageNav.navegation.navegations.push(
        {
          label: this.translationService.instant('ZURÜCK'),
          link: '/data-acquisition/work-accident'
        }
      );
    }
  }

}
