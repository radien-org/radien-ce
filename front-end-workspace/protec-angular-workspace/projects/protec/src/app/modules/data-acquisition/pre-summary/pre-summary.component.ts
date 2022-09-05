import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { Router } from '@angular/router';
import { PrimeNGConfig } from "primeng/api";
import { ConfirmationService } from 'primeng/api';
import { StorageService } from '../../../shared/services/storage/storage.service';
import { LOCAL } from '../../../shared/services/storage/local.enum';
import { Claims } from '../../../shared/models/claims/Claims';

@Component({
  selector: 'app-pre-summary',
  templateUrl: './pre-summary.component.html',
  styleUrls: ['./pre-summary.component.scss'],
  providers: [ConfirmationService]
})
export class PreSummaryComponent implements OnInit {

  pageNav = {
    navegation: {
      type: 'navegation-buttons',
      navegations: [
        {
          label: '',
          link: 'disabled'
        },
        {
          label: this.translationService.instant('home'),
          link: '/data-acquisition'
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

  editMode = true;

  currentTab = 1;

  constructor(
    private readonly translationService: TranslateService, 
    private readonly router: Router, 
    private readonly confirmationService: ConfirmationService,
    private readonly primengConfig: PrimeNGConfig,
    private readonly storageService: StorageService
  ) {
    this.claims = [];
    //this.storageService.setItem(LOCAL.CLAIMS_LIST, this.buildMock());
  }

  ngOnInit(): void {
    this.primengConfig.ripple = true;
    this.claims = this.storageService.getItem(LOCAL.CLAIMS_LIST);
    console.log('debug:', this.claims);
  }

  onSelectClaim(id: any) {
    this.leftInfo = [];
    this.claims.reverse().forEach(data => {
      //this.leftInfo = data.mainData.filter(mainData => mainData.id == "ADDITIONAL_INSURANCE_FORM");
      if(data.id == id) {
        const title = data.title;
        this.leftInfo.push({
          id: 'title',
          label: 'Title',
          value: title
        });
        const ACCIDENT_DATE_FORM: any = data.mainData.filter(mainData => mainData.id == "ACCIDENT_DATE_FORM")[0]?.value;
        this.leftInfo.push({
          id: 'diagnosis-date',
          label: 'Diagnosis date',
          value: ACCIDENT_DATE_FORM?.date
        });
        const PRIVATE_ACCIDENT_FORM: any = data.mainData.filter(mainData => mainData.id == "PRIVATE_ACCIDENT_FORM")[0]?.value;
        this.leftInfo.push({
          id: 'insurance-name',
          label: 'Insurance name',
          value: PRIVATE_ACCIDENT_FORM?.data?.howHappen
        });
        this.leftInfo.push({
          id: 'employer',
          label: 'Employer',
          value: PRIVATE_ACCIDENT_FORM?.data?.employer
        });
        this.leftInfo.push({
          id: 'last-job',
          label: 'Last Job',
          value: PRIVATE_ACCIDENT_FORM?.data?.previousEmployer
        });
        this.leftInfo.push({
          id: 'longer-than-twelve-months',
          label: '12 months held',
          value: PRIVATE_ACCIDENT_FORM?.data?.longerThanTwelveMonths ? 'Yes' : 'No'
        });
        this.leftInfo.push({
          id: 'you-do-before',
          label: 'Work before',
          value: PRIVATE_ACCIDENT_FORM?.data?.professionalActivity
        });
        this.leftInfo.push({
          id: 'you-do-before',
          label: 'Other jobs',
          value: PRIVATE_ACCIDENT_FORM?.data?.youDoBefore
        });
        console.log('debug (leftInfo):', this.leftInfo);
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
    this.editMode = !this.editMode
  }

  confirm() {
    console.log('here')
    this.confirmationService.confirm({
        message: 'Are you sure that you want to perform this action?',
        accept: () => {
          console.log('yes');
        },
        reject: () => {
          console.log('no');
        }
    });
  }

  changeTab(tab:number) {
    this.currentTab = tab;
  }

}