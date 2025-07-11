import { TestBed } from '@angular/core/testing';

import { AuthGuardNAService } from './auth-guard-na.service';

describe('AuthGuardNAService', () => {
  let service: AuthGuardNAService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AuthGuardNAService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
