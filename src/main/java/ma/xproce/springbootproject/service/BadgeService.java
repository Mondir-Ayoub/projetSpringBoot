package ma.xproce.springbootproject.service;

import ma.xproce.springbootproject.service.dtos.InscriptionDto;

public interface BadgeService {
    byte[] genererBadgePdf(InscriptionDto inscription, String nomEvent, String nomUser);
}
