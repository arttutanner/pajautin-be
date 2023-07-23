select re.program_id, re.slot, pr.maxSize, count(re.id)
FROM  participant_registration as re join on re.program_id = pr.id
group by re.program_id, re.slot;


select re.program_id, re.slot,  count(re.id) FROM  participant_registration as re group by re.program_id, re.slot;