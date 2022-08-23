import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { Router } from '@angular/router';
import { PrimeNGConfig } from "primeng/api";
import { ConfirmationService } from 'primeng/api';
import { StorageService } from '../../../shared/services/storage/storage.service';
import { LOCAL } from '../../../shared/services/storage/local.enum';
import { Claims } from '../../../shared/models/claims/Claims';


@Component({
  selector: 'app-summary',
  templateUrl: './summary.component.html',
  styleUrls: ['./summary.component.scss'],
  providers: [ConfirmationService]
})
export class SummaryComponent implements OnInit {

  pageNav = {
    navegation: {
      type: 'navegation-buttons',
      navegations: [
        {
          label: '',
          link: 'disabled'
        },
        {
          label: this.translationService.instant('weiter'),
          link: '/data-acquisition/prospects-of-success'
        }
      ]
    }
  }

  leftInfo = [
    {
      id: '',
      label: 'Label',
      value: 'Value'
    }
  ]

  claims: Array<Claims>;

  constructor(
    private readonly translationService: TranslateService, 
    private readonly router: Router, 
    private readonly confirmationService: ConfirmationService,
    private readonly primengConfig: PrimeNGConfig,
    private readonly storageService: StorageService
  ) {
    this.claims = [];
    this.storageService.setItem(LOCAL.CLAIMS_LIST, this.buildMock());
  }

  ngOnInit(): void {
    this.primengConfig.ripple = true;
    this.claims = this.storageService.getItem(LOCAL.CLAIMS_LIST);
    console.log('debug:', this.claims);
  }

  onSelectClaim(id: any) {
    console.log('id:', id);
    this.claims.forEach(data => {
      if(data.id === id) {
        this.leftInfo = data.mainData;
      }
    });
  }

  buildMock() {
    let mainData = [
      {
        id: 'insuranceName',
        label: 'Label',
        value: 'Value'
      },
      {
        id: 'employer',
        label: 'Label',
        value: 'Value'
      },
      {
        id: 'lastJob',
        label: 'Label',
        value: 'Value'
      },
      {
        id: 'employerBefore',
        label: 'Label',
        value: 'Value'
      },
      {
        id: 'twelveMonthsHeld',
        label: 'Label',
        value: 'Value'
      },
      {
        id: 'workBefore',
        label: 'Label',
        value: 'Value'
      },
      {
        id: 'otherJobs',
        label: 'Label',
        value: 'Value'
      }
    ];

    let claims = [
      {
        id: 1,
        title: 'TEST 1 CLAIM 1990',
        mainData
      },
      {
        id: 2,
        title: 'TEST 2 CLAIM 1990',
        mainData
      }
    ];

    return claims;
  }

  add() {
    this.router.navigate(['/data-acquisition']);
  }

  delete() {
    this.confirm();
  }

  edit() {
    
  }

  confirm() {
    this.confirmationService.confirm({
        message: 'Are you sure that you want to perform this action?',
        accept: () => {
            //Actual logic to perform a confirmation
        },
        reject: () => {
          //
        }
    });
  }

}
