import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { Router } from '@angular/router';
import { PrimeNGConfig } from "primeng/api";
import { ConfirmationService } from 'primeng/api';

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
      label: 'Label',
      value: 'Value'
    },
    {
      label: 'Label',
      value: 'Value'
    },
    {
      label: 'Label',
      value: 'Value'
    },
    {
      label: 'Label',
      value: 'Value'
    },
    {
      label: 'Label',
      value: 'Value'
    },
    {
      label: 'Label',
      value: 'Value'
    },
    {
      label: 'Label',
      value: 'Value'
    },
    {
      label: 'Label',
      value: 'Value'
    }
  ]

  constructor(
    private readonly translationService: TranslateService, 
    private readonly router: Router, 
    private readonly confirmationService: ConfirmationService,
    private readonly primengConfig: PrimeNGConfig
  ) { }

  ngOnInit(): void {
    this.primengConfig.ripple = true;
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

