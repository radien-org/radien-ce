import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { Router } from '@angular/router';
import { StorageService } from '../../../shared/services/storage/storage.service';
import { LOCAL } from '../../../shared/services/storage/local.enum';

@Component({
  selector: 'app-more-injuries',
  templateUrl: './more-injuries.component.html',
  styleUrls: ['./more-injuries.component.scss']
})
export class MoreInjuriesComponent implements OnInit {

  buttons = [{
    label: this.translationService.instant('Ja'),
    type: 'outline',
    link: 'function',
    funcParams: {
      choice: 'yes'
    }
  },
  {
    label: this.translationService.instant('Nein'),
    type: 'outline',
    link: 'function',
    funcParams: {
      choice: 'no'
    }
  }]

  pageNav = {
    navegation: {
      type: 'navegation-buttons',
      navegations: [
        {
          label: this.translationService.instant('ZURÜCK'),
          link: '/data-acquisition/part-body'
        }
      ]
    }
  }

  constructor(private readonly translationService: TranslateService, private readonly storageService: StorageService, private readonly router: Router) { }

  ngOnInit(): void {
    this.save();
  }

  save() {
    this.storageService.saveDataAcquisition();
  }

  selectButton(data: any) {
    console.log('debug (selectButton):', data);
    if(data.choice == 'yes'){
      this.router.navigate(['/data-acquisition/full-body']);
    } else {
      this.router.navigate(['/data-acquisition/summary']);
    }
  }

}
