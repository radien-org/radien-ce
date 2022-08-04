import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { Router } from '@angular/router';
import { StorageService } from '../../../shared/services/storage/storage.service';
import { LOCAL } from '../../../shared/services/storage/local.enum';

@Component({
  selector: 'app-accident-type',
  templateUrl: './accident-type.component.html',
  styleUrls: ['./accident-type.component.scss']
})
export class AccidentTypeComponent implements OnInit {

  buttons = [{
    label: this.translationService.instant('ARBEITSUNFALL'),
    type: 'outline',
    link: 'function',
    funcParams: {
      type: 'work-accident'
    }
  },
  {
    label: this.translationService.instant('FREIZEITUNFALL'),
    type: 'outline',
    link: 'function',
    funcParams: {
      type: 'recreational-accident'
    }
  },
  {
    label: this.translationService.instant('KRANKHEIT'),
    type: 'outline',
    link: 'function',
    funcParams: {
      type: 'disease'
    }
  }]

  pageNav = {
    navegation: {
      type: 'navegation-buttons',
      navegations: [
        {
          label: this.translationService.instant('zurück'),
          link: '/data-acquisition/accident-intro'
        }
      ]
    }
  }

  showErrorMsg = true;
  textErrorMsg = 'Lorem ipsum dolor sit amet';

  constructor(private readonly translationService: TranslateService, private readonly storageService: StorageService, private readonly router: Router) { }

  ngOnInit(): void {
  }

  selectButton(data: any) {
    this.storageService.setItem(LOCAL.ACCIDENT_TYPE, data.type)
    this.router.navigate(['/data-acquisition/additional-insurance']);
  }

}
